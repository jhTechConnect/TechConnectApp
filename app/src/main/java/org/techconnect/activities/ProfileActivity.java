package org.techconnect.activities;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import org.centum.techconnect.R;
import org.techconnect.misc.auth.AuthManager;
import org.techconnect.model.User;
import org.techconnect.sql.TCDatabaseHelper;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        User user = TCDatabaseHelper.get(this).getUser(AuthManager.get(this).getAuth().getUserId());

        //Add return arrow to action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Setup the Toolbar Title
        CollapsingToolbarLayout layout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        layout.setTitle(user.getName());

        //Add organization, email, and skills to the list below
        TextView org = (TextView) findViewById(R.id.profile_work);
        TextView email = (TextView) findViewById(R.id.profile_email);
        TextView skills = (TextView) findViewById(R.id.profile_skills);
        org.setText(user.getOrganization());
        email.setText(user.getEmail());
        skills.setText(TextUtils.join(",",user.getExpertises()));

        //Setup the edit button listeners to make changes to the profile info
        ImageButton editWork = (ImageButton) findViewById(R.id.edit_work_button);
        editWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Edit activity/fragment?
            }
        });
        ImageButton editSkill = (ImageButton) findViewById(R.id.edit_skill_button);
        editSkill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Edit Activity/Fragment
            }
        });
    }

    //This comes from the Options Menu on the upper right
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        } else if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
