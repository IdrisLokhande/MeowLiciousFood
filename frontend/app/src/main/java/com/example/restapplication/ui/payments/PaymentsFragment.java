package com.example.restapplication.ui.payments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.restapplication.databinding.FragmentPaymentsBinding;
import com.example.restapplication.Refreshable;
import com.example.restapplication.R;

import java.lang.Runnable;

public class PaymentsFragment extends Fragment implements Refreshable {

    private FragmentPaymentsBinding binding;
    private PaymentsViewModel paymentsViewModel;
    private Runnable refreshCallback;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, 
			     @Nullable Bundle savedInstanceState) {
        paymentsViewModel =
                new ViewModelProvider(this).get(PaymentsViewModel.class);

        binding = FragmentPaymentsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textPayments;
        paymentsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void refresh(Runnable onComplete){
	this.refreshCallback = onComplete;
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState){
		SwipeRefreshLayout swipeRefresh = requireActivity().findViewById(R.id.swipe_refresh);

		swipeRefresh.setOnRefreshListener(() -> {
			swipeRefresh.setRefreshing(false);
		});
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}