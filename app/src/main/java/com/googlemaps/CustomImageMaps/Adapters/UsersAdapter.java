package com.googlemaps.CustomImageMaps.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.googlemaps.CustomImageMaps.Model.User;
import com.googlemaps.CustomImageMaps.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.googlemaps.CustomImageMaps.utils.Funct.SpanTextView;
import static com.googlemaps.CustomImageMaps.utils.Funct.setImage;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.MyViewHolder> {
    public Context context;
    public ArrayList<User> users;
    public onUserListener mOnUserListener;
    User user;

    public UsersAdapter(Context context, ArrayList<User> users, onUserListener mOnUserListener, User user) {
        this.users = users;
        this.context = context;
        this.mOnUserListener = mOnUserListener;
        this.user = user;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView name;
        public ImageView img1, img2, img3, img4;
        public RelativeLayout active_indicator;
        public CardView roundCardView;
        public CircleImageView userAvatar;
        public UsersAdapter.onUserListener mListener;

        public MyViewHolder(View view, UsersAdapter.onUserListener mListener) {
            super(view);
            this.mListener = mListener;

            name = view.findViewById(R.id.username);

            userAvatar = view.findViewById(R.id.userAvatar);
            active_indicator = view.findViewById(R.id.active_indicator);
            roundCardView = view.findViewById(R.id.roundCardView);
            img1 = view.findViewById(R.id.zoom_location);
            img2 = view.findViewById(R.id.zoom_out);
            img3 = view.findViewById(R.id.user_info);
            img4 = view.findViewById(R.id.user_choices);

            active_indicator.setOnClickListener(view1 -> {
                PopupMenu popup = new PopupMenu(context, active_indicator);

                popup.getMenuInflater()
                        .inflate(R.menu.earth_popup, popup.getMenu());

                if (!users.get(getAdapterPosition()).isLoggedIn())
                    popup.getMenu().getItem(0).setTitle(context.getString(R.string.inactive));
                else
                    popup.getMenu().getItem(0).setTitle(context.getString(R.string.active));

                popup.getMenu().removeItem(R.id.item2); popup.getMenu().removeItem(R.id.item3);
                popup.show();
            });

            img1.setOnClickListener(this);
            img2.setOnClickListener(this);
            img3.setOnClickListener(this);
            img4.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.user_choices:
                    showActionsDialog(mListener, getAdapterPosition());
                    break;
                case R.id.user_info:
                    mListener.onUserDetailsClick(getAdapterPosition());
                    break;
                case R.id.zoom_location:
                    mListener.onUserZoomInClick(getAdapterPosition());
                    break;
                case R.id.zoom_out:
                    mListener.onUserZoomOutClick(getAdapterPosition());
                    break;
            }
        }

    }

    public void showActionsDialog(UsersAdapter.onUserListener mListener, int position) {
        CharSequence[] choices;

        if (user.getId().equals(users.get(position).getId()))
            choices = new CharSequence[]{context.getString(R.string.edit_profile), context.getString(R.string.my_apostoli)};
        else
            choices = new CharSequence[]{context.getString(R.string.directions_to), context.getString(R.string.apostoli), context.getString(R.string.contact)};


        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setItems(choices, (dialog, which) -> {

            if (user.getId().equals(users.get(position).getId())) {
                if (which == 0) {
                    mListener.onUserProfileUpdateClick(position);
                } else if (which == 1) {
                    mListener.onUserSendClick(position);
                }
            }
            else {
                if (which == 0) {
                    mListener.onUserDirectionsClick(position);
                } else if (which == 1) {
                    mListener.onUserSendClick(position);
                } else if (which == 2) {
                    mListener.onUserContactClick(position);
                }
            }

        });
        builder.show();
    }


    @NonNull
    @Override
    public UsersAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_list_row, parent, false);


        return new UsersAdapter.MyViewHolder(itemView, mOnUserListener);
    }


    @Override
    public void onBindViewHolder(@NonNull UsersAdapter.MyViewHolder holder, int position) {
        if (!users.get(position).isVisible()) {
            holder.itemView.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
        }
        else {
            holder.itemView.setVisibility(View.VISIBLE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
            params.setMargins(16, 0, 16, 20);

            try {
                if (!users.get(position).isLoggedIn())
                    holder.roundCardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.red));
                else
                    holder.roundCardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.green));
                CharSequence name;
                if (user.getId().equals(users.get(position).getId()))
                    name = SpanTextView(users.get(position).getlName() + " " + users.get(position).getfName(), " (Me)",
                            Color.BLACK, ContextCompat.getColor(context, R.color.edittext));
                else
                    name = users.get(position).getlName() + " " + users.get(position).getfName();
                holder.name.setText(name);
                setImage(context, holder.userAvatar, users.get(position).getImage_Url());
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
                Log.e("users_adapter","glide IndexOutOfBoundsException");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public interface onUserListener{
        void onUserProfileUpdateClick(int position);
        void onUserDirectionsClick(int position);
        void onUserSendClick(int position);
        void onUserContactClick(int position);
        void onUserDetailsClick(int position);
        void onUserZoomInClick(int position);
        void onUserZoomOutClick(int position);
    }

}
