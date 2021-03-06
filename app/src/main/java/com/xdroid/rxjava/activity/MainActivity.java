package com.xdroid.rxjava.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xdroid.rxjava.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rv;
    private List<String> fuctions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        for (int i = 0; i < 33; i++) {
            fuctions.add("Fuction " + i);
        }

        setContentView(R.layout.activity_main);
        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rv.setAdapter(new FucAdapter());
    }

    public class FucAdapter extends RecyclerView.Adapter<FucAdapter.Holder> {

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
            Holder holder = new Holder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(Holder holder, final int position) {
            String fuc = fuctions.get(position);
            holder.tv.setText(fuc);
            holder.tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position == 32) {
                        Intent intent = new Intent(MainActivity.this, RxOp6Activity.class);
                        startActivity(intent);
                    } else if (position == 31) {
                        Intent intent = new Intent(MainActivity.this, RxOp5Activity.class);
                        startActivity(intent);
                    } else if (position == 30) {
                        Intent intent = new Intent(MainActivity.this, RxOp4Activity.class);
                        startActivity(intent);
                    } else if (position == 29) {
                        Intent intent = new Intent(MainActivity.this, RxOp3Activity.class);
                        startActivity(intent);
                    } else if (position == 28) {
                        Intent intent = new Intent(MainActivity.this, RxOp2Activity.class);
                        startActivity(intent);
                    } else if (position == 27) {
                        Intent intent = new Intent(MainActivity.this, RxOp1Activity.class);
                        startActivity(intent);
                    } else if (position == 26) {
                        Intent intent = new Intent(MainActivity.this, RxActivity.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(MainActivity.this, RxDemoActivity.class);
                        intent.putExtra("Fuction", position);
                        startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return fuctions.size();
        }

        class Holder extends RecyclerView.ViewHolder {

            TextView tv;

            public Holder(View view) {
                super(view);
                tv = (TextView) view.findViewById(R.id.tv);
            }
        }
    }
}
