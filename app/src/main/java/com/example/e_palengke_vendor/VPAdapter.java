package com.example.e_palengke_vendor;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class VPAdapter extends FragmentStateAdapter {


    String id;
    String BuyerId;
    String ReceiptId;

    public VPAdapter(@NonNull FragmentActivity fragmentActivity, String id, String BuyerId, String ReceiptId) {
        super(fragmentActivity);
        this.id = id;
        this.BuyerId = BuyerId;
        this.ReceiptId = ReceiptId;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0: return new ToConfirm(id,ReceiptId,BuyerId);
            case 1: return  new ToDelivery(BuyerId,id,ReceiptId);
            case 2: return  new ToReceived(BuyerId);
            default:return  new ToConfirm(id,ReceiptId,BuyerId);
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
