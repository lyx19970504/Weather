package com.fafu.polutionrepo.finished.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.transition.Fade;
import android.transition.Slide;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.fafu.polutionrepo.finished.Adapter.City_list_Adapter;
import com.fafu.polutionrepo.finished.Adapter.MyArrayAdapter;
import com.fafu.polutionrepo.finished.Fragments.MapFragmentWeather;
import com.fafu.polutionrepo.finished.R;
import com.fafu.polutionrepo.finished.Util.Util;

import java.util.ArrayList;
import java.util.List;

public class AddCityActivity extends AppCompatActivity implements View.OnClickListener,ListView.OnItemClickListener {

    private static final String TAG = "AddCityActivity";
    private RecyclerView mCityRecyclerView;
    private List<String> provincial_capital = new ArrayList<>();
    private SearchView mSearchView;
    private ListView mListView;
    private static List<String> datas;
    private ArrayAdapter<String> arrayAdapter;
    private City_list_Adapter adapter;
    private String type;

    public void initToolbar(){
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        Toolbar toolbar = findViewById(R.id.tool_bar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.findViewById(R.id.back).setOnClickListener(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置进入退出动画
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setEnterTransition(new Fade());
        getWindow().setExitTransition(new Slide());
        setContentView(R.layout.add_city);
        datas = new ArrayList<>();
        initToolbar();
        mCityRecyclerView = findViewById(R.id.city_recycler);
        mCityRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        provincial_capital.add("当前位置");
        String[] strs = Util.readAssetsTxtFile("provincial_capital", this);
        for(int i=0;i<strs.length;i++){
            provincial_capital.add(strs[i]);
        }
        type = getIntent().getStringExtra("from_activity_type");
        adapter = new City_list_Adapter(provincial_capital,this,type);
        mCityRecyclerView.setAdapter(adapter);
        mListView = findViewById(R.id.list_view);
        arrayAdapter = new MyArrayAdapter(this,android.R.layout.simple_expandable_list_item_1,datas);
        mListView.setAdapter(arrayAdapter);
        mListView.setOnItemClickListener(this);
        mListView.setTextFilterEnabled(true);
        mSearchView = findViewById(R.id.search_view);
        mSearchView.post(new Runnable() {
            @Override
            public void run() {
                mSearchView.clearFocus();
            }
        });
        mSearchView.setSubmitButtonEnabled(false);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Toast.makeText(AddCityActivity.this, "中国没这种城市", Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(!TextUtils.isEmpty(s)){
                    mCityRecyclerView.setVisibility(View.GONE);
                    mListView.setVisibility(View.VISIBLE);
                    datas.clear();
                    List<String> returnDatas = Util.checkCityName(s);
                    datas.addAll(returnDatas);
                    arrayAdapter.notifyDataSetChanged();
                }else{
                    mCityRecyclerView.setVisibility(View.VISIBLE);
                    mListView.clearTextFilter();
                    mListView.setVisibility(View.GONE);
                    datas.clear();
                    arrayAdapter.notifyDataSetChanged();
                }
                return false;
            }
        });
    }


    @Override
    public void onClick(View v) {
        finish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String s = (String) parent.getItemAtPosition(position);
        String countyName = s.split(",")[0];
        if(type.equals(ChooseCityActivity.TAG)) {
            Intent intent = MainActivity.newIntent(this, countyName);
            startActivity(intent);
        }else if(type.equals(MapFragmentWeather.TAG)){
            Intent intent = new Intent();
            intent.putExtra(MainActivity.CITY_NAME,countyName);
            setResult(RESULT_OK,intent);
        }
        finish();
    }
}
