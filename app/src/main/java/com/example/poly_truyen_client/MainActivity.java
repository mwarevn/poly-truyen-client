package com.example.poly_truyen_client;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.poly_truyen_client.api.ConnectAPI;
import com.example.poly_truyen_client.models.Comic;
import com.example.poly_truyen_client.notifications.NotificationEvent;
import com.example.poly_truyen_client.notifications.NotifyConfig;
import com.example.poly_truyen_client.socket.SocketConfig;
import com.example.poly_truyen_client.socket.SocketManager;
import com.example.poly_truyen_client.socket.SocketSingleton;
import com.example.poly_truyen_client.ui.history.HistoryFragment;
import com.example.poly_truyen_client.ui.home.HomeFragment;
import com.example.poly_truyen_client.ui.settings.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.poly_truyen_client.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationBarView;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashMap;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity {
    private Socket socket;
    private ActivityMainBinding binding;

    private HashMap<String, Fragment> fragments = new HashMap<>();
    private Fragment activeFragment;


    private void switchFragment(int itemId, BottomNavigationView navView) {
        Fragment selectedFragment = null;

        if (itemId == R.id.navigation_home) {
            selectedFragment = fragments.get("home");
        }

        if (itemId == R.id.navigation_history) {
            selectedFragment = fragments.get("history");
        }

        if (itemId == R.id.navigation_settings) {
            selectedFragment = fragments.get("settings");
        }

        if (selectedFragment != null && selectedFragment != activeFragment) {
            if (navView.getSelectedItemId() != itemId) {
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_activity_main, selectedFragment).commit();
                activeFragment = selectedFragment;

            }
//            getSupportFragmentManager().beginTransaction().hide(activeFragment).show(selectedFragment).commit();
//            activeFragment = selectedFragment;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SocketManager socketManager = SocketManager.getInstance(new ConnectAPI().API_URL);
        socket = socketManager.getSocket();
        socket.on("ServerPostNewComic", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifi("Poly comic đã đăng một truyện mới!", args[0].toString());
                    }
                });
            }
        });

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_history,
                R.id.navigation_home, R.id.navigation_settings)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);



//        fragments.put("home", new HomeFragment());
//        fragments.put("history", new HistoryFragment());
//        fragments.put("settings", new SettingsFragment());
//
//        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//        for (Fragment fragment : fragments.values()) {
//            fragmentTransaction.add(R.id.nav_host_fragment_activity_main, fragment).hide(fragment);
//        }
//        activeFragment = fragments.get("home");
//        fragmentTransaction.show(activeFragment).commit();
//        BottomNavigationView navView = findViewById(R.id.nav_view);
//        navView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
//                switchFragment(menuItem.getItemId(), navView);
//                return true;
//            }
//        });


    }

    void notifi(String title, String comicData) {
        Comic comic = new Gson().fromJson(comicData, Comic.class);

        Intent intent = new Intent(MainActivity.this, ViewDetailsComicActivity.class);
        intent.putExtra("comic", comicData);

        PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Notification customNotification = new NotificationCompat.Builder(MainActivity.this, NotifyConfig.CHANEL_ID)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle(title)
                .setContentText(comic.getName())
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(MainActivity.this);

        if (ActivityCompat.checkSelfPermission(MainActivity.this,
                android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 999999);
            Toast.makeText(MainActivity.this, "Vui lòng cấp quyền gửi thông báo!", Toast.LENGTH_SHORT).show();
            return;
        }

        int id_notiy = (int) new Date().getTime();
        notificationManagerCompat.notify(id_notiy, customNotification);
    }


}