package com.example.linnpodcastradio.ui.podcast_home;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.linnpodcastradio.R;
import com.example.linnpodcastradio.databinding.PodcastHomeFragmentBinding;
import com.example.linnpodcastradio.model.Podcast;
import com.example.linnpodcastradio.viewmodel.PodcastViewModel;

import java.util.List;

import static androidx.core.content.ContextCompat.getSystemService;

public class PodcastHomeFragment extends Fragment {
    private PodcastViewModel viewModel;
    private PodcastHomeRecyclerAdapter podcastRecyclerAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SearchView searchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        PodcastHomeFragmentBinding binding = DataBindingUtil.inflate(inflater, R.layout.podcast_home_fragment,container,false);
        View view = binding.getRoot();
        ViewModelProvider.NewInstanceFactory factory = new ViewModelProvider.NewInstanceFactory();
        viewModel = new ViewModelProvider(getViewModelStore(), factory).get(PodcastViewModel.class);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);
        setupRecycler(view);
        setupSearchView(view);
        setupObservers();
        setupSwipeRefresh(view);
        checkNetworkConnection();
        return view;
    }

    private void setupRecycler(View view){
        RecyclerView recyclerView = view.findViewById(R.id.podcast_home_recycler);
        podcastRecyclerAdapter = new PodcastHomeRecyclerAdapter(recyclerView, viewModel,getFragmentManager());
        recyclerView.setAdapter(podcastRecyclerAdapter);
    }

    private void setupSearchView(View view){
        searchView = view.findViewById(R.id.podcast_home_search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                viewModel.searchPodcasts(query);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.equals("")){
                    viewModel.clearSearch();
                }
                return true;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                viewModel.clearSearch();
                return true;
            }
        });
    }

    private void setupObservers(){
        viewModel.getLiveTopPodcasts().observe(this, new Observer<List<Podcast>>() {
            @Override
            public void onChanged(List<Podcast> podcasts) {
                podcastRecyclerAdapter.setItems(podcasts);
            }
        });
        viewModel.getLiveSearchPodcasts().observe(this, new Observer<List<Podcast>>() {
            @Override
            public void onChanged(List<Podcast> podcasts) {
                if (searchView.getQuery().toString().equals("")){
                    podcastRecyclerAdapter.setItems(viewModel.getTopPodcasts());
                }
                else{
                    if(!podcasts.isEmpty()){
                        podcastRecyclerAdapter.setItems(podcasts);
                    }
                }
            }
        });
    }

    private void setupSwipeRefresh(View view){
        swipeRefreshLayout = view.findViewById(R.id.podcast_home_swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                if(isNetworkOnline()){
                    viewModel.refresh();
                }
                else{
                    Toast.makeText(getContext(),"Please connect to a network and refresh", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void checkNetworkConnection(){
        if(isNetworkOnline()){
            viewModel.refresh();
        }
        else{
            Toast.makeText(getContext(),"Please connect to a network and refresh", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isNetworkOnline() {
        boolean status=false;
        try{
            ConnectivityManager cm = (ConnectivityManager) getSystemService(requireContext(),ConnectivityManager.class);
            NetworkInfo netInfo = cm.getNetworkInfo(0);
            if (netInfo != null && netInfo.getState()==NetworkInfo.State.CONNECTED) {
                status= true;
            }
            else {
                netInfo = cm.getNetworkInfo(1);
                if(netInfo!=null && netInfo.getState()==NetworkInfo.State.CONNECTED)
                    status= true;
            }
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return status;
    }
}
