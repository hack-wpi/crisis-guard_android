package com.shltr.darrieng.shltr_android;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.shltr.darrieng.shltr_android.Pojo.UserPojo;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NearbyPersonAdapter extends RecyclerView.Adapter<NearbyPersonAdapter.ViewHolder> {

    Context context;

    List<UserPojo> userList;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.profile_pic)
        ImageView profilePic;

        @BindView(R.id.user_name)
        TextView userNameView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    /**
     * COOOONSTRUCTOR
     *
     * @param context  Android context.
     * @param userList List of users and their info.
     */
    public NearbyPersonAdapter(Context context, List<UserPojo> userList) {
        this.context = context;
        this.userList = userList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(
            parent.getContext()).inflate(R.layout.profile_row, parent, false);

        return new ViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Picasso.with(context).load(
            userList.get(position).getPicture()).resize(72, 72).into(holder.profilePic);

        holder.userNameView.setText(userList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void addUser(UserPojo newUser) {
        for (UserPojo pojo : userList) {
            if (pojo.getPicture().equals(newUser.getPicture())) {
                return;
            }
        }

        userList.add(newUser);
        notifyItemChanged(userList.size() - 1);
        Toast.makeText(context, "Adding user", Toast.LENGTH_SHORT).show();
    }
}

