package com.example.healthcare.home;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.healthcare.DialogHelper;
import com.example.healthcare.DoctorData;
import com.example.healthcare.FlowLayout;
import com.example.healthcare.HeaderHelper;
import com.example.healthcare.MainActivity;
import com.example.healthcare.R;
import com.example.healthcare.appointment.DoctorProfileFragment;

import java.util.ArrayList;
import java.util.List;

public class FindDoctorFragment extends Fragment {

    private static final String ARG_TAB = "tab";

    private static final String[] DEPARTMENTS = {"All", "Cardiology", "Orthopedics", "Neurology", "Dermatology"};

    private static final String[] SYMPTOMS = {
            "Fever", "Headache", "Cough", "Cold", "Sore Throat", "Chest Pain", "Breathlessness",
            "Body Ache", "Fatigue", "Nausea", "Vomiting", "Diarrhea", "Constipation", "Stomach Pain",
            "Back Pain", "Joint Pain", "Skin Rash", "Itching", "Allergy", "Dizziness", "Migraine",
            "High BP", "Diabetes Checkup", "Eye Irritation", "Ear Pain", "Toothache", "Anxiety",
            "Insomnia", "Urinary Issue", "Swelling"
    };

    private static final String[] GENDER_OPTIONS = {"Any", "Male", "Female"};

    private TextView tabDoctor, tabSymptom;
    private View panelDoctor, panelSymptom;
    private EditText etSearch;
    private LinearLayout doctorList;
    private String selectedDept = "All";

    public static FindDoctorFragment newInstance(int tab) {
        FindDoctorFragment f = new FindDoctorFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_TAB, tab);
        f.setArguments(b);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_find_doctor, container, false);
        final MainActivity main = (MainActivity) requireActivity();
        HeaderHelper.bind(main, v, "Find a Doctor", null, true, false);

        tabDoctor = v.findViewById(R.id.tabByDoctor);
        tabSymptom = v.findViewById(R.id.tabBySymptom);
        panelDoctor = v.findViewById(R.id.panelDoctor);
        panelSymptom = v.findViewById(R.id.panelSymptom);
        etSearch = v.findViewById(R.id.etSearch);
        doctorList = v.findViewById(R.id.doctorList);

        tabDoctor.setOnClickListener(x -> selectTab(0));
        tabSymptom.setOnClickListener(x -> selectTab(1));

        buildDepartmentChips(v, inflater);
        buildFilterChips(v);
        buildDoctorCards(inflater);
        buildSymptomChips(v, inflater);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                applyDoctorFilter();
            }
        });

        int startTab = getArguments() != null ? getArguments().getInt(ARG_TAB, 0) : 0;
        selectTab(startTab);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) requireActivity()).setSelectedTab(MainActivity.TAB_NONE);
    }

    private void selectTab(int tab) {
        boolean symptom = tab == 1;
        tabDoctor.setSelected(!symptom);
        tabSymptom.setSelected(symptom);
        panelDoctor.setVisibility(symptom ? View.GONE : View.VISIBLE);
        panelSymptom.setVisibility(symptom ? View.VISIBLE : View.GONE);
        etSearch.setHint(symptom ? "Search a symptom" : "Search doctor or department");
        etSearch.setText("");
    }

    // ------------------------------------------------------------ doctor tab

    private void buildDepartmentChips(View root, LayoutInflater inflater) {
        final FlowLayout flow = root.findViewById(R.id.deptChips);
        final List<TextView> chips = new ArrayList<>();
        for (final String dept : DEPARTMENTS) {
            final TextView chip = (TextView) inflater.inflate(R.layout.item_chip, flow, false);
            chip.setText(dept);
            chip.setSelected(dept.equals(selectedDept));
            chip.setOnClickListener(x -> {
                selectedDept = dept;
                for (TextView c : chips) c.setSelected(c == chip);
                applyDoctorFilter();
            });
            chips.add(chip);
            flow.addView(chip);
        }
    }

    private void buildFilterChips(View root) {
        final TextView gender = root.findViewById(R.id.chipGender);
        final TextView date = root.findViewById(R.id.chipDate);
        final TextView time = root.findViewById(R.id.chipTime);

        gender.setOnClickListener(x -> new AlertDialog.Builder(requireActivity())
                .setTitle("Gender")
                .setItems(GENDER_OPTIONS, (dialog, which) -> {
                    gender.setText(which == 0 ? "Gender" : GENDER_OPTIONS[which]);
                    gender.setSelected(which != 0);
                })
                .show());
        date.setOnClickListener(x -> {
            DialogHelper.pickDate(requireActivity(), date, true);
            date.setSelected(true);
        });
        time.setOnClickListener(x -> {
            DialogHelper.pickSlot(requireActivity(), time);
            time.setSelected(true);
        });
    }

    private void buildDoctorCards(LayoutInflater inflater) {
        final MainActivity main = (MainActivity) requireActivity();
        for (final DoctorData d : DoctorData.ALL) {
            View card = inflateDoctorCard(inflater, doctorList, d,
                    d.dept + " \u00B7 " + d.years + " yrs exp", d.available);
            card.setTag(d);
            card.setOnClickListener(x -> main.navigateTo(DoctorProfileFragment.newInstance(d.name)));
            doctorList.addView(card);
        }
    }

    private void applyDoctorFilter() {
        String q = etSearch.getText().toString().trim().toLowerCase();
        for (int i = 0; i < doctorList.getChildCount(); i++) {
            View card = doctorList.getChildAt(i);
            DoctorData d = (DoctorData) card.getTag();
            boolean deptOk = selectedDept.equals("All") || selectedDept.equals(d.dept);
            boolean textOk = q.isEmpty()
                    || d.name.toLowerCase().contains(q)
                    || d.dept.toLowerCase().contains(q);
            card.setVisibility(deptOk && textOk ? View.VISIBLE : View.GONE);
        }
    }

    // ------------------------------------------------------------ symptom tab

    private void buildSymptomChips(View root, LayoutInflater inflater) {
        final FlowLayout flow = root.findViewById(R.id.symptomChips);
        final List<TextView> chips = new ArrayList<>();
        for (int i = 0; i < SYMPTOMS.length; i++) {
            final TextView chip = (TextView) inflater.inflate(R.layout.item_chip, flow, false);
            chip.setText(SYMPTOMS[i]);
            chip.setSelected(i == 0);
            chip.setOnClickListener(x -> {
                for (TextView c : chips) c.setSelected(c == chip);
            });
            chips.add(chip);
            flow.addView(chip);
        }

        final MainActivity main = (MainActivity) requireActivity();
        LinearLayout result = root.findViewById(R.id.symptomResult);
        final DoctorData best = DoctorData.ALL[0];
        View card = inflateDoctorCard(inflater, result, best, "Best match \u00B7 4\u20136 PM slots", true);
        card.setOnClickListener(x -> main.navigateTo(DoctorProfileFragment.newInstance(best.name)));
        result.addView(card);
    }

    // ------------------------------------------------------------ shared

    private View inflateDoctorCard(LayoutInflater inflater, ViewGroup parent, DoctorData d,
                                   String meta, boolean available) {
        View card = inflater.inflate(R.layout.item_doctor_card, parent, false);
        ((TextView) card.findViewById(R.id.docInitials)).setText(d.initials);
        ((TextView) card.findViewById(R.id.docName)).setText(d.name);
        ((TextView) card.findViewById(R.id.docMeta)).setText(meta);

        TextView badge = card.findViewById(R.id.docBadge);
        badge.setText(available ? "Available" : "Full");
        badge.setBackgroundResource(available ? R.drawable.bg_badge_green : R.drawable.bg_badge_red);
        badge.setTextColor(requireContext().getColor(
                available ? R.color.badge_green_text : R.color.badge_red_text));
        return card;
    }
}
