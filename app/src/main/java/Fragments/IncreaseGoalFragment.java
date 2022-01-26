package Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tall_tale.R;

//allow user to increase their goal
public class IncreaseGoalFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

      View view = inflater.inflate(R.layout.fragment_increase_goal, container, false);

      return view;
    }
}