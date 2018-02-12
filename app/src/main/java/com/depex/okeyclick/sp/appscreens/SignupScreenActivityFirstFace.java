package com.depex.okeyclick.sp.appscreens;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.depex.okeyclick.sp.R;
import com.depex.okeyclick.sp.adapters.CitySpinnerAdapter;
import com.depex.okeyclick.sp.adapters.CountrySpinnerAdapter;
import com.depex.okeyclick.sp.adapters.StateSpinnerAdapter;
import com.depex.okeyclick.sp.api.APICallback;
import com.depex.okeyclick.sp.api.ApiListener;
import com.depex.okeyclick.sp.api.ProjectAPI;
import com.depex.okeyclick.sp.api.SignupSerailization;
import com.depex.okeyclick.sp.constants.StringUtils;
import com.depex.okeyclick.sp.constants.Utils;
import com.depex.okeyclick.sp.modal.City;
import com.depex.okeyclick.sp.modal.Country;
import com.depex.okeyclick.sp.modal.State;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.apache.commons.validator.routines.EmailValidator;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignupScreenActivityFirstFace extends AppCompatActivity implements ApiListener<JsonObject>, AdapterView.OnItemSelectedListener, View.OnClickListener {


    @BindView(R.id.country_spinner)
    Spinner countrySpinner;
    @BindView(R.id.city_spinner)
    Spinner citySpinner;
    @BindView(R.id.state_spinner)
    Spinner stateSpinner;
    @BindView(R.id.first_name_signup)
    EditText first_name_signup;
    @BindView(R.id.last_name_signup)
    EditText last_name_signup;
    @BindView(R.id.password_signup)
    EditText password_signup;
    @BindView(R.id.re_pass_signup)
    EditText re_type_signup;
    @BindView(R.id.address_signup)
    EditText address_signup;
    @BindView(R.id.email_signup)
    EditText email_signup;
    @BindView(R.id.next_signup)
    Button next_signup;
    @BindView(R.id.zip_signup)
    EditText zip_code;

    ProjectAPI api;
    APICallback apiCallback;

    SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_screen_first_face);
        ButterKnife.bind(this);
        preferences=getSharedPreferences("service_pref", MODE_PRIVATE);

        String pref=preferences.getString("first_", "0");
        if(!pref.equals("0")){
            try {
                JSONObject object=new JSONObject(pref);
                boolean isCompleteFirstFase=object.getBoolean("first_fase");
                if(isCompleteFirstFase){
                    Intent intent=new Intent(this, SignupScreenActivitySecondFace.class);
                    startActivity(intent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        initSpinner();

        next_signup.setOnClickListener(this);

        Retrofit.Builder retrofitBuilder=new Retrofit.Builder();
        Retrofit retrofit=retrofitBuilder.baseUrl(Utils.SITE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        api=retrofit.create(ProjectAPI.class);
        Call<JsonObject> countryCall=api.getCountries(getString(R.string.apikey));
        apiCallback=new APICallback(this);
        countryCall.enqueue(apiCallback);
    }

    private void initSpinner() {
        ArrayList<City> cities=new ArrayList<>();
        City city=new City();
        city.setCityName("City");
        cities.add(city);

        ArrayList<Country> countries=new ArrayList<>();
        Country country=new Country();
        country.setName("Country");
        countries.add(country);

        ArrayList<State> states=new ArrayList<>();
        State state=new State();
        state.setStateName("State");
        states.add(state);

        CitySpinnerAdapter citySpinnerAdapter=new CitySpinnerAdapter(this, cities);
        CountrySpinnerAdapter countrySpinnerAdapter=new CountrySpinnerAdapter(countries, this);
        StateSpinnerAdapter stateSpinnerAdapter =new StateSpinnerAdapter(this, states);

        stateSpinner.setAdapter(stateSpinnerAdapter);
        citySpinner.setAdapter(citySpinnerAdapter);
        countrySpinner.setAdapter(countrySpinnerAdapter);


        stateSpinner.setOnItemSelectedListener(this);
        citySpinner.setOnItemSelectedListener(this);
        countrySpinner.setOnItemSelectedListener(this);
    }


    public void clearSpinner(Spinner spinner){

        ArrayList<City> cities=new ArrayList<>();
        City city=new City();
        city.setCityName("City");
        cities.add(city);

        ArrayList<Country> countries=new ArrayList<>();
        Country country=new Country();
        country.setName("Country");
        countries.add(country);

        ArrayList<State> states=new ArrayList<>();
        State state=new State();
        state.setStateName("State");
        states.add(state);

        switch (spinner.getId()){
            case R.id.city_spinner:
                CitySpinnerAdapter citySpinnerAdapter=new CitySpinnerAdapter(this, cities);
                citySpinner.setAdapter(citySpinnerAdapter);
                break;
            case R.id.state_spinner:
                StateSpinnerAdapter stateSpinnerAdapter=new StateSpinnerAdapter(this, states);
                stateSpinner.setAdapter(stateSpinnerAdapter);
                break;
            case R.id.country_spinner:
                CountrySpinnerAdapter countrySpinnerAdapter=new CountrySpinnerAdapter(countries, this);
                countrySpinner.setAdapter(countrySpinnerAdapter);
                break;
        }
    }

    @Override
    public void success(Call<JsonObject> call, JsonObject response, Object... objects) {
        if(response==null)
            return;
        Log.i(this.getClass().getName(), response.toString());
        boolean success=response.get("successBool").getAsBoolean();
        if(success){
            String responseType=response.get("responseType").getAsString();
            JsonObject responseObj=response.getAsJsonObject("response");
            Gson gson=new Gson();
            switch (responseType){
                case "country_list":
                    JsonArray country_list=responseObj.getAsJsonArray("List");
                    Country[]country=gson.fromJson(country_list, Country[].class);
                    ArrayList<Country> countries=new ArrayList<>(Arrays.asList(country));
                    Country firstCountry=new Country();
                    firstCountry.setName("Country");
                    countries.add(0, firstCountry);
                    CountrySpinnerAdapter adapter=new CountrySpinnerAdapter(countries, this);
                    countrySpinner.setAdapter(adapter);
                    Log.i("retrofit2Json", countries+"");
                    break;
                case "state_list":
                    JsonArray state_list=responseObj.getAsJsonArray("List");
                    State[]state=gson.fromJson(state_list, State[].class);
                    ArrayList<State> states=new ArrayList<>(Arrays.asList(state));
                    State firstState=new State();
                    firstState.setStateName("State");
                    states.add(0, firstState);
                    StateSpinnerAdapter stateSpinnerAdapter=new StateSpinnerAdapter( this, states);
                    stateSpinner.setAdapter(stateSpinnerAdapter);
                    break;

                case "get_city":
                    JsonArray city_list=responseObj.getAsJsonArray("List");
                    City[]city=gson.fromJson(city_list, City[].class);
                    ArrayList<City> cities=new ArrayList<>(Arrays.asList(city));
                    City firstCity=new City();
                    firstCity.setCityName("City");
                    cities.add(0, firstCity);
                    CitySpinnerAdapter citySpinnerAdapter=new CitySpinnerAdapter(this, cities);
                    citySpinner.setAdapter(citySpinnerAdapter);
                    break;

            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        switch (adapterView.getId()){
            case R.id.country_spinner:
                clearSpinner(stateSpinner);
                clearSpinner(citySpinner);
                CountrySpinnerAdapter adapter= (CountrySpinnerAdapter) adapterView.getAdapter();
                Country country=adapter.getCountries().get(position);
                String countryId=country.getId();
                Call<JsonObject> stateCall=api.getStates(getString(R.string.apikey), countryId);
                stateCall.enqueue(apiCallback);
                break;
            case R.id.city_spinner:

                break;
            case R.id.state_spinner:
                clearSpinner(citySpinner);
                StateSpinnerAdapter stateSpinnerAdapter= (StateSpinnerAdapter) adapterView.getAdapter();
                State state=stateSpinnerAdapter.getStates().get(position);
                String stateID=state.getStateID();
                Log.i("retrofit2Json", stateID+"");
                Call<JsonObject> cityCall=api.getCities(getString(R.string.apikey), stateID);
                cityCall.enqueue(apiCallback);
                break;
        }
    }



    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.next_signup:
                SignupSerailization signupSerailization=new SignupSerailization();
                String name= first_name_signup.getText().toString();

                String email=email_signup.getText().toString();
                String password=password_signup.getText().toString();
                String retypePassword=re_type_signup.getText().toString();
                String  address=address_signup.getText().toString();
                String  country= ((Country) countrySpinner.getSelectedItem()).getId();
                String city=((City)citySpinner.getSelectedItem()).getCityID();
                String state=((State)stateSpinner.getSelectedItem()).getStateID();
                String zip=zip_code.getText().toString();

                if(!StringUtils.isValid(name)){
                   show("Enter a valid name !");
                    return;
                }

                EmailValidator emailValidator=EmailValidator.getInstance();
                if(!emailValidator.isValid(email)){
                    show("Enter a valid email Address!");
                    return;
                }

                if(StringUtils.isEmpty(password)){
                    show("Please Fill password minimum 6 Character !");
                    return;
                }

                if(!password.equals(retypePassword)){
                    show("Password does not matched !");
                    return;
                }

                if(StringUtils.isEmpty(address)){
                    show("Please Fill your address !");
                    return;
                }

                if(country==null){
                    show("Please Select Country !");
                    return;
                }

                if(state==null){
                    show("Please Select State !");
                    return;
                }

                if(city==null){
                    show("Please Select City !");
                    return;
                }
                if(!StringUtils.isValidPin(zip)){
                    show("please enter a valid pin code");
                    return;
                }


                try {
                    JSONObject requestData=new JSONObject();
                    JSONObject data=new JSONObject();
                    data.put("v_code", getString(R.string.v_code));
                    data.put("apikey", getString(R.string.apikey));
                    data.put("city", city);
                    data.put("country", country);
                    data.put("password", password);
                    data.put("state", state);
                    data.put("email", email);
                    data.put("first_name", first_name_signup.getText().toString());
                    data.put("last_name", last_name_signup.getText().toString());
                    data.put("signupWith", "");requestData.put("requestData", data);
                    requestData.put("first_fase", true );

                    preferences.edit().putString("first_",requestData.toString()).commit();

                    Intent signUpSecond=new Intent(this, SignupScreenActivitySecondFace.class);
                    startActivity(signUpSecond);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    public void show(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }



}