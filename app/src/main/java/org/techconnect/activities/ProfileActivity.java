package org.techconnect.activities;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
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
        final TextView org = (TextView) findViewById(R.id.profile_work);
        final TextView email = (TextView) findViewById(R.id.profile_email);
        org.setText(user.getOrganization());
        email.setText(user.getEmail());

        //Create all rows from list of skills in profile
        final TableLayout skills_table = (TableLayout) findViewById(R.id.skills_table);
        for (String skill : user.getExpertises()) {
            TableRow toAdd = (TableRow) getLayoutInflater().inflate(R.layout.skill_tablerow,null,false);
            TextInputLayout addSkill = (TextInputLayout) toAdd.findViewById(R.id.edit_skill_layout);
            addSkill.setVisibility(View.GONE);
            TextView toAddText = (TextView)  toAdd.findViewById(R.id.skill_text);
            toAddText.setText(skill);
            toAddText.setVisibility(View.VISIBLE);
            skills_table.addView(toAdd);
        }

        //Register the edit text fields for changing account info
        final TextInputLayout edit_org_layout = (TextInputLayout) findViewById(R.id.edit_work_layout);
        final TextInputLayout edit_email_layout = (TextInputLayout) findViewById(R.id.edit_email_layout);
        final EditText edit_org = (EditText) findViewById(R.id.edit_work_text);
        final EditText edit_email = (EditText) findViewById(R.id.edit_email_text);
        edit_org.setText(user.getOrganization());
        edit_email.setText(user.getEmail());

        //Setup the edit button listeners to make changes to the profile info
        final ImageButton editWork = (ImageButton) findViewById(R.id.edit_work_button);
        editWork.setOnClickListener(new View.OnClickListener() {
            boolean isEditing = false;
            @Override
            public void onClick(View view) {
                if (!isEditing) {
                    org.setVisibility(View.GONE);
                    email.setVisibility(View.GONE);
                    edit_org_layout.setVisibility(View.VISIBLE);
                    edit_email_layout.setVisibility(View.VISIBLE);
                    editWork.setImageResource(R.drawable.ic_done_black_24dp);
                } else {
                    org.setVisibility(View.VISIBLE);
                    email.setVisibility(View.VISIBLE);
                    org.setText(edit_org.getText());
                    email.setText(edit_email.getText());

                    edit_org_layout.setVisibility(View.GONE);
                    edit_email_layout.setVisibility(View.GONE);
                    editWork.setImageResource(R.drawable.ic_mode_edit_black_24dp);
                }
                isEditing = !isEditing;
            }
        });
        final ImageButton addSkill = (ImageButton) findViewById(R.id.add_skill_button);
        addSkill.setOnClickListener(new View.OnClickListener() {
            boolean isAdding = false;
            TableRow toAdd;
            TextInputLayout inputLayout;
            ImageView icon;
            EditText add_skill;
            TextView skill_text;
            @Override
            public void onClick(View view) {
                //Add a new row to the list of skills
                if (!isAdding) {
                    toAdd = (TableRow) getLayoutInflater().inflate(R.layout.skill_tablerow,null,false);
                    inputLayout = (TextInputLayout) toAdd.findViewById(R.id.edit_skill_layout);
                    icon = (ImageView) toAdd.findViewById(R.id.skill_icon);
                    add_skill = (EditText) toAdd.findViewById(R.id.edit_skill_text);
                    icon.setImageResource(R.drawable.ic_add_box_black_24dp);
                    add_skill.setText("Add Skill");
                    addSkill.setImageResource(R.drawable.ic_done_black_24dp);
                    skills_table.addView(toAdd);
                } else { //Stopping adding
                    toAdd = (TableRow) skills_table.getChildAt(skills_table.getChildCount()- 1 );
                    skill_text = (TextView) toAdd.findViewById(R.id.skill_text);
                    skill_text.setText(add_skill.getText());

                    icon.setImageResource(R.drawable.ic_build_black_24dp);
                    skill_text.setVisibility(View.VISIBLE);
                    inputLayout.setVisibility(View.GONE);
                    addSkill.setImageResource(R.drawable.ic_add_box_black_24dp);
                }
                isAdding = !isAdding;
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
