package com.acktos.regalosquehablan.transporter.presentation;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.acktos.regalosquehablan.transporter.R;
import com.acktos.regalosquehablan.transporter.controllers.BaseController;
import com.acktos.regalosquehablan.transporter.controllers.OrdersController;
import com.acktos.regalosquehablan.transporter.controllers.TransportersController;
import com.acktos.regalosquehablan.transporter.models.Route;
import com.acktos.regalosquehablan.transporter.presentation.adapters.RoutesAdapter;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

public class RoutesFragment extends Fragment implements RoutesAdapter.OnRecyclerViewClickListener {

    //Attributes
    private List<Route> routes;

    //Components
    OrdersController ordersController;

    //UI references
    private RecyclerView routesRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter recyclerAdapter;

    //Listeners
    private OnRouteSelectedListener mListener;


    public static RoutesFragment newInstance(String param1, String param2) {
        RoutesFragment fragment = new RoutesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public RoutesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_routes, container, false);

        //Initialize attributes
        routes=new ArrayList<>();

        //Initialize UI
        routesRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_routes);
        routesRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        routesRecyclerView.setLayoutManager(layoutManager);
        recyclerAdapter = new RoutesAdapter(getActivity(),routes,this);
        routesRecyclerView.setAdapter(recyclerAdapter);


        ordersController=new OrdersController(getActivity());
        ordersController.getOrders(new Response.Listener<List<Route>>() {
            @Override
            public void onResponse(List<Route> responseRoutes) {

                if (responseRoutes != null) {

                    Log.i(BaseController.TAG_DEBUG,"routes in fragment:"+responseRoutes.toString());
                    routes.clear();
                    routes.addAll(responseRoutes);
                    recyclerAdapter.notifyDataSetChanged();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            //mListener.onRouteSelected(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnRouteSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onRecyclerViewClick(View v, int position) {

        Log.i(BaseController.TAG_DEBUG,"click on route "+ position);
        mListener.onRouteSelected(routes.get(position));
    }


    public interface OnRouteSelectedListener {
        public void onRouteSelected(Route route);
    }

}
