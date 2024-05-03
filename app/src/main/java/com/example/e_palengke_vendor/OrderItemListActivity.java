package com.example.e_palengke_vendor;

import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OrderItemListActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;

    RecyclerView productListView;

    ProductListAdapter adapter;

    ArrayList<ProductModel> ProductItems;

    VPAdapter vpAdapter;
    TabLayout tabLayout;
    ViewPager2 viewPager2;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_item_list);

        //Sustaining Value from the previous class
        String id = getIntent().getStringExtra("UserId");
        String ReceiptId = getIntent().getStringExtra("ReceiptId");
        String BuyerId = getIntent().getStringExtra("BuyerId");

        tabLayout = findViewById(R.id.tabLayoutPurchaseOrder);
        viewPager2 = findViewById(R.id.vpPurchaseOrder);
        vpAdapter = new VPAdapter(this,id,BuyerId,ReceiptId);

        viewPager2.setAdapter(vpAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.getTabAt(position).select();
            }
        });




    }
}