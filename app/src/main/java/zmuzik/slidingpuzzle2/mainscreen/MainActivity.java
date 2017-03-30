package zmuzik.slidingpuzzle2.mainscreen;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.StringTokenizer;

import javax.inject.Inject;

import zmuzik.slidingpuzzle2.App;
import zmuzik.slidingpuzzle2.Conf;
import zmuzik.slidingpuzzle2.R;
import zmuzik.slidingpuzzle2.adapters.MainScreenPagerAdapter;
import zmuzik.slidingpuzzle2.di.components.DaggerMainActivityComponent;
import zmuzik.slidingpuzzle2.di.components.MainActivityComponent;
import zmuzik.slidingpuzzle2.di.modules.MainScreenModule;


public class MainActivity extends AppCompatActivity implements MainScreenView {

    final String TAG = this.getClass().getSimpleName();

    ViewPager mViewPager;

    MainActivityComponent mComponent;

    @Inject
    MainScreenPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mComponent = DaggerMainActivityComponent.builder()
                .appComponent(((App) getApplication()).getComponent(this))
                .mainScreenModule(new MainScreenModule(this))
                .build();
        mComponent.inject(this);
        mComponent.inject(mPresenter);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setTitle(null);
            actionBar.setSubtitle(null);
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.title_section1)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.title_section2)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.title_section3)));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        if (mViewPager != null) {
            mViewPager.setAdapter(new MainScreenPagerAdapter(this));
        }

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    public MainActivityComponent getDiComponent() {
        return mComponent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_change_grid_size:
                openChangeGridSizeDialog();
                return true;
            case R.id.action_toggle_display_numbers:
                mPresenter.toggleShowNumbers();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void openChangeGridSizeDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.select_grid_size));
        builder.setSingleChoiceItems(Conf.GRID_SIZES, getGridDimsPosition(), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                String positionsStr = Conf.GRID_SIZES[item];
                StringTokenizer tokenizer = new StringTokenizer(positionsStr, "x");
                String shorterStr = tokenizer.nextToken();
                String longerStr = tokenizer.nextToken();
                mPresenter.setGridDimensions(Integer.parseInt(shorterStr), Integer.parseInt(longerStr));
                dialog.dismiss();
                String msg = String.format(App.get().getString(R.string.grid_size_selected_to), positionsStr);
                Toast.makeText(App.get(), msg, Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }

    private int getGridDimsPosition() {
        String currentDims = mPresenter.getGridDimensions();
        for (int i = 0; i < Conf.GRID_SIZES.length; i++) {
            if (currentDims.equals(Conf.GRID_SIZES[i])) return i;
        }
        return 0;
    }


}
