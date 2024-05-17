package com.example.e_palengke_vendor;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class VPAdapterPaid extends FragmentStateAdapter {


    String id;
    String BuyerId;

    public VPAdapterPaid(@NonNull FragmentActivity fragmentActivity, String id, String BuyerId ) {
        super(fragmentActivity);
        this.id = id;
        this.BuyerId = BuyerId;

    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0: return new ToConfirmPaid(id,BuyerId);
            case 1: return  new ToDeliveryPaid(BuyerId,id);
            case 2: return  new ToReceivePaid(BuyerId);
            default:return  new ToConfirmPaid(id,BuyerId);
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}