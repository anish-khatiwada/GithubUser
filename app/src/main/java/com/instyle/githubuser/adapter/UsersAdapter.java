package com.instyle.githubuser.adapter;

import android.content.Context;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.instyle.githubuser.DetailActivity;
import com.instyle.githubuser.R;
import com.instyle.githubuser.model.Users;
import com.instyle.githubuser.model.displayUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class UsersAdapter extends
        RecyclerView.Adapter<UsersAdapter.ViewHolder> implements Filterable {

    private static final String TAG = UsersAdapter.class.getSimpleName();

    private Context context;
    private List<Users> userslist;
    private List<displayUser> displaylist;
    private List<displayUser> contactListFiltered;
    private AdapterCallback mAdapterCallback;

    public UsersAdapter(Context context, List<Users> list, AdapterCallback adapterCallback) {
        this.context = context;
        this.userslist = list;

        this.mAdapterCallback = adapterCallback;
    }
    public UsersAdapter(Context context, List<displayUser> list) {
        this.context = context;
        this.displaylist = list;
        this.contactListFiltered = list;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_item,
                parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
       final displayUser user = displaylist.get(position);

        holder._userName.setText(user.getUserName());


        Picasso.get().load(user.getProfileImage()).into(holder.profile_image);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = holder.itemView.getContext();
                Intent intent = new Intent(context, DetailActivity.class);

                intent.putExtra("user", user.getUserName());
                intent.putExtra("userProfile", user.getProfileImage());
                intent.putExtra("html_url", user.getHtml_url());
                context.startActivity(intent);

                Log.i("result",user.getUserName());
                Log.i("result",user.getProfileImage());
                Log.i("result",user.getHtml_url());


            }
        });
    }

    @Override
    public int getItemCount() {
        return displaylist.size();
    }

    public void clear() {
        int size = this.userslist.size();
        this.userslist.clear();
        notifyItemRangeRemoved(0, size);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.user_name)
        TextView _userName;
        @BindView(R.id.profile_image)
        CircleImageView profile_image;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }

    public interface AdapterCallback {
        void onRowClicked(int position);
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();

                Log.i("charString",charString);
                if (charString.isEmpty()) {
                    contactListFiltered = displaylist;
                } else {
                    List<displayUser> filteredList = new ArrayList<>();

                    for (displayUser row : displaylist) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for user name
                        if (row.getUserName().toLowerCase().contains(charString.toLowerCase()) ) {
                            filteredList.add(row);
                        }
                    }

                    Log.i("searchdata",filteredList.toString());

                    contactListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = contactListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                contactListFiltered = (ArrayList<displayUser>) filterResults.values;
                notifyDataSetChanged();
            }
        };


}}
