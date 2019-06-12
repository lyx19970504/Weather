package com.fafu.polutionrepo.finished.Fragments;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.fafu.polutionrepo.finished.Beans.User;
import com.fafu.polutionrepo.finished.R;

import java.util.ArrayList;
import java.util.List;

public class MyFocusedPeopleFragment extends Fragment {

    private RecyclerView mRecyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_focused_people_list, container,false);
        mRecyclerView = view.findViewById(R.id.people_list);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(manager);
        List<User> users = new ArrayList<>();
        for(int i=1;i<=10;i++){
            User user = new User();
            user.setUsername("测试用户"+i);
            users.add(user);
        }
        PeopleAdapter adapter = new PeopleAdapter(users);
        mRecyclerView.setAdapter(adapter);
        return view;
    }

    private class PeopleAdapter extends RecyclerView.Adapter<PeopleAdapter.PeopleHolder> {

        private List<User> mUsers;

        public PeopleAdapter(List<User> users){
            mUsers = users;
        }
        @NonNull
        @Override
        public PeopleHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.people_list_item,viewGroup,false);
            PeopleHolder holder = new PeopleHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull final PeopleHolder peopleHolder, int i) {
            User user = mUsers.get(i);
            peopleHolder.mPeopleName.setText(user.getUsername());
            peopleHolder.mPeoplePortrait.setImageResource(R.drawable.taiji);
            peopleHolder.mFocusButton.setSelected(true);
            peopleHolder.mFocusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        Toast.makeText(getContext(), v.isSelected() ? "取消关注成功!" : "关注成功!", Toast.LENGTH_SHORT).show();
                        peopleHolder.mFocusButton.setText(v.isSelected() ? "关注" : "已关注");
                        peopleHolder.mFocusButton.setSelected(!v.isSelected());
                        peopleHolder.mFocusButton.setBackgroundColor(
                                v.isSelected() ? Color.rgb(0,199,212) : Color.rgb(255, 152, 0));
                }
            });
        }

        @Override
        public int getItemCount() {
            return mUsers.size();
        }

        class PeopleHolder extends RecyclerView.ViewHolder{

            private ImageView mPeoplePortrait;
            private TextView mPeopleName;
            private Button mFocusButton;

            public PeopleHolder(@NonNull View itemView) {
                super(itemView);
                mPeopleName = itemView.findViewById(R.id.people_name);
                mPeoplePortrait = itemView.findViewById(R.id.people_portrait);
                mFocusButton = itemView.findViewById(R.id.focus_button);
                mFocusButton.setBackgroundColor(Color.rgb(0,199,212));
            }
        }
    }
}
