package com.fafu.polutionrepo.finished.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fafu.polutionrepo.finished.Activities.CityImageDetailActivity;
import com.fafu.polutionrepo.finished.Activities.CityViewActivity;
import com.fafu.polutionrepo.finished.Activities.SearchCityViewActivity;
import com.fafu.polutionrepo.finished.Beans.Topic;
import com.fafu.polutionrepo.finished.R;
import com.fafu.polutionrepo.finished.Util.Util;
import com.fafu.polutionrepo.finished.Util.WeatherUtil;
import com.safframework.injectview.Injector;
import com.safframework.injectview.annotations.InjectView;

import org.json.JSONArray;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class TopicFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "TopicFragment";
    @InjectView(R.id.search_cityView)
    LinearLayout mLinearLayout;
    @InjectView(R.id.topic_list)
    LinearLayout mTopicList;
    public static final String TOPIC_URL= Util.URL_PREFIX + "/green/getTopics?num=100&offset=0";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.topic_layout, container,false);
        Injector.injectInto(this,view);
        initViews();
        return view;
    }

    public void initViews(){
        mLinearLayout.setOnClickListener(this);
        Util.getTopicsRequest(TOPIC_URL, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: "+"获取话题列表失败!");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.body() != null) {
                    String responseText = response.body().string();
                    List<Topic> topics = WeatherUtil.handleTopics(responseText);
                    Log.d(TAG, "onResponse: "+topics.size());
                    if(topics.size() != 0){
                        for(final Topic topic : topics){
                            final View view = View.inflate(getContext(), R.layout.topic_item, null);
                            TextView titleTextView = view.findViewById(R.id.title);
                            titleTextView.setText(topic.getTitle());
                            TextView detailedInfoView = view.findViewById(R.id.detailed_info);
                            detailedInfoView.setText(topic.getShortInfo());
                            view.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = CityImageDetailActivity.newIntent(getContext(),topic);
                                    startActivity(intent);
                                }
                            });
                            if(getActivity() != null){
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mTopicList.addView(view);
                                    }
                                });
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.search_cityView:
                Intent intent = new Intent(getContext(), SearchCityViewActivity.class);
                startActivity(intent);
                break;
        }
    }
}
