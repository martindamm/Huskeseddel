package org.projects.shoppinglist;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.client.Firebase;
import com.firebase.ui.FirebaseListAdapter;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import android.support.v7.widget.ShareActionProvider;

public class MainActivity extends AppCompatActivity {
    ArrayAdapter<Product> adapter;
    ListView listView;
    Product lastDeletedProduct;
    int lastDeletedPosition;
    Firebase mRef;
    Firebase m2Ref;

    private ShareActionProvider actionProvider;
    private EditText inputText;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    // Firebase Adapter.

    FirebaseListAdapter<Product> fireAdapter;
    public FirebaseListAdapter getMyAdapter() {
        return fireAdapter;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mRef = new Firebase("https://huskeliste-8a3f7.firebaseio.com");
        m2Ref = new Firebase("https://huskeliste-8a3f7.firebaseio.com");

        listView = (ListView) findViewById(R.id.list);
         fireAdapter =
                new FirebaseListAdapter<Product>(
                        this,
                        Product.class,
                        android.R.layout.simple_list_item_checked,
                        mRef
                ) {
                    @Override
                    protected void populateView(View view, Product product, int i) {
                    TextView textView = (TextView) view.findViewById(android.R.id.text1);
                    textView.setText(product.toString());
                    }
                };
        listView.setAdapter(fireAdapter);
        // CHOICE_MODE_SINGLE = kun et valg
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        // Spinners gør det muligt at vælge en værdi ud fra et sæt af værdier.
        // Den henter data fra howmanyarray via ArrayAdapter.createFromResource
        final Spinner howmanyspinner= (Spinner) findViewById(R.id.howmanyspinner);
        ArrayAdapter<CharSequence> adp3=ArrayAdapter.createFromResource(this,
                R.array.howmanyarray, android.R.layout.simple_list_item_1);

        adp3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        howmanyspinner.setAdapter(adp3);
        howmanyspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                // TODO Auto-generated method stub
                String ss = howmanyspinner.getSelectedItem().toString();
                Toast.makeText(getBaseContext(), ss, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

        // addButton
        Button addButton = (Button) findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override

            // Følgende kode tager værdien af input feltet (EditText), samt
            // antallet og tilføjer den til Firebase --> mRef.push().setValue(p);
            public void onClick(View v) {
                EditText itemTxt = (EditText) findViewById(R.id.itemInput);
                String ss=howmanyspinner.getSelectedItem().toString();
                Integer howMany = Integer.parseInt(ss);
                Product p = new Product(itemTxt.getText().toString(), howMany);
                mRef.push().setValue(p);
                itemTxt.setText("");
                howmanyspinner.setSelection(0);

                // Når data bliver tilføjet ændres info, og derfor
                // bliver vi nødt til at genindlæse ændringerne med
                // nedenstående linje:
                getMyAdapter().notifyDataSetChanged();
            }
        });


        // deleteButton som tilhører snackbar.
        // Snackbar giver lidt feedback til en operation. Beskeden bliver vist nederst
        // på skærmen.

        Button deleteButton = (Button) findViewById(R.id.deleteButton);
        final View parent = findViewById(R.id.layout);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCopy(); //save a copy of the deleted item
                int index = listView.getCheckedItemPosition();
                getMyAdapter().getRef(index).setValue(null);
                getMyAdapter().notifyDataSetChanged(); //notify view

                Snackbar snackbar = Snackbar
                        .make(parent, "Slet "+lastDeletedProduct+"?", Snackbar.LENGTH_LONG)
                        .setAction("Slet ikke", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mRef.push().setValue(lastDeletedProduct);
                                getMyAdapter().notifyDataSetChanged();
                                Snackbar snackbar = Snackbar.make(parent, lastDeletedProduct+" er tilbage på listen igen",
                                        Snackbar.LENGTH_SHORT);
                                snackbar.show();
                            }
                        });
                snackbar.show();
            }
        });


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        Colorout();
    }

    // Slettet værdi tilføjes til lastDeletedProduct. Hvis der fortrydes hentes
    // værdien tilbage derfra.
    public Product getItem(int index)
    {
        return (Product) getMyAdapter().getItem(index);
    }
    public void saveCopy()
    {
        lastDeletedPosition = listView.getCheckedItemPosition();
        lastDeletedProduct= getItem(lastDeletedPosition);

    }

    // 'Del' funktionen. Konverterer listen til en string
    // og kører et for-loop hvor den for hver forekomst
    // fra Firebase returnerer item og kvantitet.
    public String convertListToString()
    {
        String result = "";
        for (int i = 0; i<fireAdapter.getCount();i++)
        {
            Product p = (Product) fireAdapter.getItem(i);
            String sp =p.toString();
            result += p.getQuantity()+" "+p.getName() + "\n";
             Log.d("her er din liste", sp);
        }
        return result;
    }

    // Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
                case R.id.action_settings:
                Toast.makeText(this, "Instillinger", Toast.LENGTH_SHORT)
                    .show();
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivityForResult(intent, 1);

                return true;
            // Info popup
            case R.id.item_about:
                Toast.makeText(this, "Mobile Mandatory, \nDaniel Buus & Martin Damm", Toast.LENGTH_LONG)
                    .show();
                return true;

            // Share
            case R.id.share:
            int id = item.getItemId();
            if (id==R.id.share)
            {
                Intent intentshare = new Intent(Intent.ACTION_SEND);
                intentshare.setType("text/plain");
                int totalItems = fireAdapter.getCount();
                SharedPreferences prefs = getSharedPreferences("my_prefs", MODE_PRIVATE);
                String navn = prefs.getString("name", "");
                String textToShare =navn+"'s Huskeseddel. Du skal huske "+ totalItems +" ting:\n" +convertListToString();
                intentshare.putExtra(Intent.EXTRA_TEXT, textToShare);//add the text to t
                intentshare.putExtra(android.content.Intent.EXTRA_SUBJECT, "HuskeSeddel");
                startActivity(intentshare);
            }
            return true;


            // Clear items from list.
            case R.id.item_clear:
                //public void showDialog(View v) {

                // DialogFragment bruges til at vise en dialog ovenpå en aktivitet.
                MyDialogFragment dialog = new MyDialogFragment() {
                    @Override
                    protected void positiveClick() {
                        //m2Ref.push().setValue(null);
                        m2Ref.removeValue();
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Listen blev slettet", Toast.LENGTH_SHORT);
                        toast.show();
                        getMyAdapter().notifyDataSetChanged();
                    }

                    @Override
                    protected void negativeClick() {
                        //Here we override the method and can now do something
                        Toast toast = Toast.makeText(getApplicationContext(),
                         "negative button clicked", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                };

               dialog.show(getFragmentManager(), "MyFragment");

                return true;
        }

        return false;
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }
    @Override
    public void onStart() {


        super.onStart();

        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://org.projects.shoppinglist/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://org.projects.shoppinglist/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==1)
        {
            Colorout();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // Baggrundsindstillinger
    // if(theme.equals("White") skal matche theme_values i strings.xml for at virke.
    public void Colorout (){
        SharedPreferences prefs = getSharedPreferences("my_prefs", MODE_PRIVATE);
        String theme = prefs.getString("theme", "");
        String name = prefs.getString("name", "");
        Toast.makeText(
                this,
                name + "Baggrundsfarven er "+  theme+ " nu", Toast.LENGTH_SHORT).show();
        LinearLayout ln = (LinearLayout) this.findViewById(R.id.layout);
        ln.setBackgroundColor(Color.rgb(255, 255, 255));
        if(theme.equals("White")){
            ln.setBackgroundColor(Color.rgb(255,255,255));
        }
        else if(theme.equals("Bisque")){
            ln.setBackgroundColor(Color.rgb(205,183,158));
        }
        else if(theme.equals("Dark Orange")){
            ln.setBackgroundColor(Color.rgb(255,140,0));
        }
        else if(theme.equals("Steel Blue")){
            ln.setBackgroundColor(Color.rgb(70,130,180));
        }

    }
    public void setPreferences(View v) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivityForResult(intent, 1);
    }

    public void getPreferences(View v) {
        SharedPreferences prefs = getSharedPreferences("my_prefs", MODE_PRIVATE);
        String email = prefs.getString("email", "");
        String theme = prefs.getString("theme", "");
        boolean soundEnabled = prefs.getBoolean("sound", false);

        Toast.makeText(
                this,
                "Email: " + email + "\nTheme: " + theme + "\nSound Enabled: "
                        + soundEnabled, Toast.LENGTH_SHORT).show();
    }
}