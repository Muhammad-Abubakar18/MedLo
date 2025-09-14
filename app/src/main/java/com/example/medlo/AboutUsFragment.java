package com.example.medlo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

public class AboutUsFragment extends Fragment {

    public AboutUsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about_us, container, false);

        // Set lavender background color
        view.setBackgroundColor(Color.parseColor("#E6E6FA"));

        // Initialize team member 1
        ImageView member1Image = view.findViewById(R.id.member1_image);
        TextView member1Name = view.findViewById(R.id.member1_name);
        TextView member1Role = view.findViewById(R.id.member1_role);
        TextView member1Bio = view.findViewById(R.id.member1_bio);

        member1Image.setImageResource(R.drawable.ic_member);
        member1Name.setText("Muhammad Abubakar");
        member1Role.setText("Lead Developer");
        member1Bio.setText("10+ years of experience in Android development. Passionate about creating user-friendly apps.");

        // Initialize team member 2
        ImageView member2Image = view.findViewById(R.id.member2_image);
        TextView member2Name = view.findViewById(R.id.member2_name);
        TextView member2Role = view.findViewById(R.id.member2_role);
        TextView member2Bio = view.findViewById(R.id.member2_bio);

        member2Image.setImageResource(R.drawable.ic_member);
        member2Name.setText("Mahnoor Adnan");
        member2Role.setText("UI/UX Designer");
        member2Bio.setText("Specializes in creating beautiful and intuitive interfaces. Loves minimalist design.");

        // Initialize team member 3
        ImageView member3Image = view.findViewById(R.id.member3_image);
        TextView member3Name = view.findViewById(R.id.member3_name);
        TextView member3Role = view.findViewById(R.id.member3_role);
        TextView member3Bio = view.findViewById(R.id.member3_bio);

        member3Image.setImageResource(R.drawable.ic_member);
        member3Name.setText("Fahad Abbasi");
        member3Role.setText("Backend Developer");
        member3Bio.setText("Expert in server-side development and database management. Ensures smooth app performance.");

        return view;
    }
}