package sg.edu.rp.c346.smsretriever;


import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.PermissionChecker;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentFirst extends Fragment {
    Button btnAddText, btnEmail;
    TextView tvFrag1, tv;
    String smsBody;
EditText et;
    public FragmentFirst() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_first, container, false);

        tvFrag1 = (TextView) view.findViewById(R.id.tvFrag1);
        btnAddText = (Button) view.findViewById(R.id.btnRetrieve1);
        tv = (TextView) view.findViewById(R.id.tv1);
        et = (EditText)view.findViewById(R.id.etNum);
        btnEmail = (Button)view.findViewById(R.id.btnEmail1);
        btnAddText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int permissionCheck = PermissionChecker.checkSelfPermission
                        (getContext(), Manifest.permission.READ_SMS);

                if (permissionCheck != PermissionChecker.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.READ_SMS}, 0);
                    // stops the action from proceeding further as permission not
                    //  granted yet
                    return;
                }

                // Create all messages URI
                Uri uri = Uri.parse("content://sms");
// The columns we want
                //  date is when the message took place
                //  address is the number of the other party
                //  body is the message content
                //  type 1 is received, type 2 sent
                String[] reqCols = new String[]{"date", "address", "body", "type"};

                // Get Content Resolver object from which to
                //  query the content provider
                ContentResolver cr = getActivity().getContentResolver();

                String tt = et.getText().toString();
                String parts[] = tt.split(" ");

                String filter = null;
                String[] filterArgs = null;

                if (et.getText().equals("") == false){
                    if(parts.length > 1){
                        filter = "body";
                        filterArgs = new String[parts.length];
                        for(int i = 0; i<parts.length;i++){
                            if(i>0){
                                filter += " OR body";
                            }
                            filter += " LIKE ?";
                            filterArgs[i] = "%" + parts[i] + "%";
                        }
                    }else if(parts.length == 1){
                        filter="body LIKE ?";
                        // The matches for the ?
                        filterArgs = new String[]{"%" + et.getText().toString() + "%"};
                    }

                }else{
                }
                // Fetch SMS Message from Built-in Content Provider
                Cursor cursor = cr.query(uri, reqCols, filter, filterArgs, null);
                smsBody = "";
                if(cursor.moveToFirst()) {
                    do {
                        long dateInMillis = cursor.getLong(0);
                        String date = (String) DateFormat
                                .format("dd MMM yyyy h:mm:ss aa", dateInMillis);
                        String address = cursor.getString(1);
                        String body = cursor.getString(2);
                        String type = cursor.getString(3);
                        if (type.equalsIgnoreCase("1")) {
                            type = "Inbox:";
                        } else {
                            type = "Sent:";
                        }
                        smsBody += type + " " + address + "\n at " + date
                                + "\n\"" + body + "\"\n\n";
                    } while (cursor.moveToNext());
                }
                tv.setText(smsBody);
            }
        });
        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent email = new Intent(Intent.ACTION_SEND);
                // Put essentials like email address, subject & body text
                email.putExtra(Intent.EXTRA_EMAIL,
                        new String[]{"jason_lim@rp.edu.sg"});
                email.putExtra(Intent.EXTRA_SUBJECT,
                        "Test Email from C347");
                email.putExtra(Intent.EXTRA_TEXT,
                        smsBody);
                // This MIME type indicates email
                email.setType("message/rfc822");
                // createChooser shows user a list of app that can handle
                // this MIME type, which is, email
                startActivity(Intent.createChooser(email,
                        "Choose an Email client :"));


            }
        });
        return view;
    }

}
