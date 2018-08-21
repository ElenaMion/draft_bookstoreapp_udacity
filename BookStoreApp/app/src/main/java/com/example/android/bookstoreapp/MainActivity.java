package com.example.android.bookstoreapp;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity {
    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.tabs)
    TabLayout tabs;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;

    Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);

        myViewPagerAdapter = new MyViewPagerAdapter(getSupportFragmentManager());

        myViewPagerAdapter.addFragment(new BookFragment(), getString(R.string.books_tab_name));
        myViewPagerAdapter.addFragment(new SupplierFragment(), getString(R.string.suppliers_tab_name));
        myViewPagerAdapter.addFragment(new DeliveryFragment(), getString(R.string.deliveries_tab_name));

        // Set the adapter onto the view pager
        viewPager.setAdapter(myViewPagerAdapter);

        // Connect the tab layout with the view pager.
        tabs.setupWithViewPager(viewPager);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("EXTRA_PAGE");
            int position = Integer.parseInt(value);
            viewPager.setCurrentItem(position);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}

