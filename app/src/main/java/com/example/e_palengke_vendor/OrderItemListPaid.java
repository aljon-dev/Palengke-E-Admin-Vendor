package com.example.e_palengke_vendor;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;

public class OrderItemListPaid extends AppCompatActivity {

    VPAdapterPaid vpAdapter;
    TabLayout tabLayout;
    ViewPager2 viewPager2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_order_item_list_paid);





        //Sustaining Value from the previous class
        String id = getIntent().getStringExtra("UserId");
        String BuyerId = getIntent().getStringExtra("BuyerId");

        tabLayout = findViewById(R.id.tabLayoutPurchaseOrder);
        viewPager2 = findViewById(R.id.vpPurchaseOrder);
        vpAdapter = new VPAdapterPaid(this,id,BuyerId);

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