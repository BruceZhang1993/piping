package com.shinado.piping.geek;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shinado.piping.geek.header.IHeadView;
import com.shinado.piping.geek.store.ShoppingActivity;

import shinado.indi.vender.R;

/**
 * Created by shinado on 16/5/11.
 */
public class ShoppingHeadView implements IHeadView {

    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public View getView(final Context context, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_shop, null);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                context.startActivity(new Intent(context, ShoppingActivity.class));
            }
        });
        return view;
    }

    @Override
    public void notifyUI() {

    }
}
