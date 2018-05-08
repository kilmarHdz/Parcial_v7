package com.cabrera.parcial_v7;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.io.Serializable;

public class Fragment_Contacto_Click extends Fragment implements Serializable {
    ImageView Profile;
    TextView Name;
    RadioButton Number;
    TextView Email;
    TextView Type;
    ImageButton Call;
    ImageButton Share;
    String path;
    RadioGroup TotalNumbers;
    private static final int PERMISSIONS_REQUEST_PHONE_CALL = 101;
    Contacto C;

    public Fragment_Contacto_Click() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Profile = getActivity().findViewById(R.id.ImageClicked);
        Name = getActivity().findViewById(R.id.NameClicked);
        Type = getActivity().findViewById(R.id.TypeClicked);
        Email = getActivity().findViewById(R.id.EmailClicked);
        Call = getActivity().findViewById(R.id.SendCall);
        Share = getActivity().findViewById(R.id.Share);
        TotalNumbers = getActivity().findViewById(R.id.TotalNumberClicked);
        float spTextSize = 12;
        float textSize = spTextSize * getResources().getDisplayMetrics().scaledDensity;
        if (C != null && Profile!= null) {
            if (C.getImageUri() == null) {
                path = "";
                Profile.setImageResource(R.drawable.ic_account_box);
            } else {
                path = C.getImageUri();
                Profile.setImageURI(Uri.parse(C.getImageUri()));
            }

            //Name.setText(C.getName());
            for(String c : C.getNumber()){
                RadioGroup.LayoutParams lparams = new RadioGroup.LayoutParams(
                        RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
                RadioButton tv=new RadioButton(getActivity());
                tv.setLayoutParams(lparams);
                tv.setGravity(Gravity.CENTER);
                tv.setText(c);
                tv.setTextSize(textSize);
                this.TotalNumbers.addView(tv);
            }
            Type.setText(C.getType());
            Email.setText(C.getEmail());
            Call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int radioButtonID = TotalNumbers.getCheckedRadioButtonId();
                    if(radioButtonID!=-1){
                        Number= getActivity().findViewById(radioButtonID);
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + Number.getText().toString().replace("-", "")));
                        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, PERMISSIONS_REQUEST_PHONE_CALL);
                        } else startActivity(intent);
                    }
                }
            });

            Share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int radioButtonID = TotalNumbers.getCheckedRadioButtonId();
                    if(radioButtonID!=-1) {
                        Number= getActivity().findViewById(radioButtonID);
                        Uri imageUri;
                        if (path == "")
                            imageUri = Uri.parse("android.resource://com.cabrera.parcial_v7/drawable/ic_account_circle");
                        else
                            imageUri = Uri.parse(path);

                        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                        emailIntent.setType("image/*");
                        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.ShareFrag));
                        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.NombreFrag) + Name.getText() + "\n" +
                                getString(R.string.TelFrag) + Number.getText() + "\n" + getString(R.string.EmailFrag) + Email.getText());
                        emailIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                        startActivity(Intent.createChooser(emailIntent, getString(R.string.Enviando)));
                    }
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onViewCreated(container, savedInstanceState);

        Intent intent = getActivity().getIntent();
        Bundle extras = getArguments();
        if (savedInstanceState != null) {
            C = savedInstanceState.getParcelable("KEY");
        } else {
            if (intent.hasExtra("KEY")) {
                C =  intent.getExtras().getParcelable("KEY");
            }
            if (extras!=null) {
                C = extras.getParcelable("KEY");
            }
        }

        return inflater.inflate(R.layout.fragment_clickcontacto, container, false);
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("KEY", C);
    }


}