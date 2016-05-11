package com.shinado.piping.geek;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shinado.piping.geek.header.IHeadView;

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
    public View getView(Context context, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_shop, null);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        return view;
    }

    @Override
    public void notifyUI() {

    }
}
