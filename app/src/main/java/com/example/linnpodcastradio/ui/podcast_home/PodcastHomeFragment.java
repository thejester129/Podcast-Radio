package com.example.linnpodcastradio.ui.podcast_home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

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

public class PodcastHomeFragment extends Fragment {
    private PodcastViewModel viewModel;
    private PodcastTopTenRecyclerAdapter podcastRecyclerAdapter;
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
        return view;
    }

    private void setupRecycler(View view){
        RecyclerView recyclerView = view.findViewById(R.id.podcast_home_recycler);
        podcastRecyclerAdapter = new PodcastTopTenRecyclerAdapter(recyclerView, viewModel,getFragmentManager());
        recyclerView.setAdapter(podcastRecyclerAdapter);
    }

    private void setupSearchView(View view){
        searchView = view.findViewById(R.id.podcast_home_search_view);
        searchView.setIconified(false);
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
                    podcastRecyclerAdapter.setItems(podcasts);
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
                viewModel.refresh();
            }
        });
    }
}
