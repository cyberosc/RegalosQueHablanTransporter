package com.acktos.regalosquehablan.transporter.presentation;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.acktos.regalosquehablan.transporter.R;
import com.acktos.regalosquehablan.transporter.controllers.BaseController;
import com.acktos.regalosquehablan.transporter.models.Order;
import com.acktos.regalosquehablan.transporter.models.Route;
import com.acktos.regalosquehablan.transporter.presentation.adapters.OrdersAdapter;
import com.acktos.regalosquehablan.transporter.presentation.adapters.RoutesAdapter;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.acktos.regalosquehablan.transporter.presentation.OrdersFragment.OnOrderSelectedListener} interface
 * to handle interaction events.
 * Use the {@link OrdersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrdersFragment extends Fragment implements RoutesAdapter.OnRecyclerViewClickListener {

    //Attributes
    private List<Order> orders;
    private Route route;

    //UI references
    private RecyclerView ordersRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter recyclerAdapter;
    private MaterialDialog deliveryStartDialog;

    //Listeners
    private OnOrderSelectedListener orderListener;


    public static OrdersFragment newInstance(String routeObject) {
        OrdersFragment fragment = new OrdersFragment();
        Bundle args = new Bundle();
        args.putString(Route.KEY_MODEL, routeObject);

        fragment.setArguments(args);
        return fragment;
    }

    public OrdersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String routeObject = getArguments().getString(Route.KEY_MODEL);

            try {
                JSONObject jsonObject=new JSONObject(routeObject);
                route=new Route(jsonObject);
                orders=route.getOrders();

            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(BaseController.TAG_DEBUG, "error trying to convert route object to json object");
            }

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_orders, container, false);

        //Initialize UI
        ordersRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_orders);
        ordersRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        ordersRecyclerView.setLayoutManager(layoutManager);
        recyclerAdapter = new OrdersAdapter(getActivity(),orders,this);
        ordersRecyclerView.setAdapter(recyclerAdapter);


        return view;
    }


    /*public void onButtonPressed(Uri uri) {
        if (orderListener != null) {
            orderListener.onOrderSelected(uri);
        }
    }*/

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            orderListener = (OnOrderSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        orderListener = null;
    }

    @Override
    public void onRecyclerViewClick(View v, int position) {

        Log.i(BaseController.TAG_DEBUG,"id btn:"+v.getId());

        if(v.getId()==R.id.btn_delivery_card){

            if(deliveryStartDialog!=null){
                deliveryStartDialog.show();
            }else{
                showDeliveryStartDialog(position);
            }
        }

        if(v.getId()==R.id.btn_info_card){
            Intent i=new Intent(getActivity(),DetailActivity.class);
            i.putExtra(Order.KEY_ORDER,orders.get(position).toString());
            startActivity(i);
        }

    }

    private void showDeliveryStartDialog(final int position){

        deliveryStartDialog=new MaterialDialog.Builder(getActivity())
                .title(R.string.confirm_start_delivery)
                .content(R.string.msg_confirm_start_delivery)
                .positiveText(R.string.accept)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        Log.i(BaseController.TAG_DEBUG,"entry to start delivery");
                        orderListener.onOrderSelected(orders.get(position));
                    }
                })
                .show();
    }

    public interface OnOrderSelectedListener {
         void onOrderSelected(Order order);
    }

}
