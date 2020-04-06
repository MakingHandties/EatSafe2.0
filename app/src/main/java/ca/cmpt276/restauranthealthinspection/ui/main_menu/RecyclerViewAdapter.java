package ca.cmpt276.restauranthealthinspection.ui.main_menu;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import ca.cmpt276.restauranthealthinspection.R;
import ca.cmpt276.restauranthealthinspection.model.Restaurant;
import ca.cmpt276.restauranthealthinspection.model.RestaurantManager;
import ca.cmpt276.restauranthealthinspection.ui.restaurant_details.RestaurantDetails;

/**
 * RecyclerViewAdapter defines how RecyclerView in main menu will setup each Card view.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RestaurantCardViewHolder> {

    private Context context;
    private RestaurantManager restaurantManager;

    RecyclerViewAdapter(Context context, RestaurantManager restaurantManager) {
        this.restaurantManager = restaurantManager;
        this.context = context;
    }

    @NonNull
    @Override
    public RestaurantCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.info_window_restaurant, parent, false);
        return new RestaurantCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantCardViewHolder holder, int position) {
        Restaurant restaurant = restaurantManager.get(position);
        holder.setupCardView(restaurant);

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String trackingID = restaurant.getResTrackingNumber();
                Intent i = RestaurantDetails.makeLaunchIntent(context, trackingID);
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return restaurantManager.restaurantCount();
    }

    /**
     * RestaurantCardViewHolder setup restaurant cards to be added to Recycler View.
     */
    class RestaurantCardViewHolder extends RecyclerView.ViewHolder {

        private final ImageView hazardIcon = itemView.findViewById(R.id.infoWindowHazardIcon);
        private final TextView textViewRestaurantName = itemView.findViewById(R.id.infoWindowRestaurantName);
        private final TextView textViewInspectionDate = itemView.findViewById(R.id.infoWindowInspectionDate);
        private final TextView textViewHazardLevel = itemView.findViewById(R.id.infoWindowHazardLevel);
        private final TextView textViewAddress = itemView.findViewById(R.id.infoWindowAddress);
        private final TextView textViewIssuesCount = itemView.findViewById(R.id.infoWindowIssuesNum);
        private final CardView parentLayout = itemView.findViewById(R.id.infoWindowParentViewCardView);
        private final CardView warningBar = itemView.findViewById(R.id.infoWindowWarningBar);
        private final ImageView logoIV = itemView.findViewById(R.id.logoImageView);

        private RestaurantCardViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        private void setupCardView(Restaurant restaurant) {

            textViewRestaurantName.setText(restaurant.getName());
            textViewAddress.setText(restaurant.getAddress());
            logoIV.setImageResource(restaurant.getLogo());

            //background colour for favourite restaurants, temporary
            if (restaurantManager.isFavourite(restaurant.getResTrackingNumber())) {
                parentLayout.setBackgroundColor(Color.parseColor("#fffd70"));
            }

            if (restaurant.hasBeenInspected()) {
                String latestInspectionDate = restaurant.getLatestInspectionDate(context);
                textViewInspectionDate.setText(latestInspectionDate);

                String totalIssues = restaurant.getLatestInspectionTotalIssues();
                textViewIssuesCount.setText(totalIssues);

                RestaurantListActivity.HazardLevel hazardLevel = HazardLevelConverter(restaurant.getHazardLevel(context));
                setupWarningBar(hazardLevel);
            } else {
                //default setup for restaurant with no inspections.
                textViewInspectionDate.setText(R.string.no_inspections_found);
                textViewIssuesCount.setText(R.string.no);
                setupWarningBar(RestaurantListActivity.HazardLevel.LOW);
            }
        }

        private void setupWarningBar(RestaurantListActivity.HazardLevel hazardLevel) {

            switch (hazardLevel) {
                case LOW:
                    textViewHazardLevel.setText(R.string.hazard_level_low);
                    warningBar.setCardBackgroundColor(context.getColor(R.color.hazardLowDark));
                    hazardIcon.setImageDrawable(context.getDrawable(R.drawable.icon_hazard_low));
                    break;
                case MEDIUM:
                    textViewHazardLevel.setText(R.string.hazard_level_medium);
                    warningBar.setCardBackgroundColor(context.getColor(R.color.hazardMediumDark));
                    hazardIcon.setImageDrawable(context.getDrawable(R.drawable.icon_hazard_medium));
                    break;
                case HIGH:
                    textViewHazardLevel.setText(R.string.hazard_level_high);
                    warningBar.setCardBackgroundColor(context.getColor(R.color.hazardHighDark));
                    hazardIcon.setImageDrawable(context.getDrawable(R.drawable.icon_hazard_high));
                    break;
            }
        }
    }

    private RestaurantListActivity.HazardLevel HazardLevelConverter(String hazardLevel) {
        if (hazardLevel.equals(context.getString(R.string.hazard_rating_low))) {
            return RestaurantListActivity.HazardLevel.LOW;
        } else if (hazardLevel.equals(context.getString(R.string.hazard_rating_medium))) {
            return RestaurantListActivity.HazardLevel.MEDIUM;
        } else if (hazardLevel.equals(context.getString(R.string.hazard_rating_high))) {
            return RestaurantListActivity.HazardLevel.HIGH;
        }

        // Will crash the program if this part is reached
        return null;
    }
}
