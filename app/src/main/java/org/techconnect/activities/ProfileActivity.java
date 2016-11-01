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
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.centum.techconnect.R;
import org.techconnect.misc.auth.AuthManager;
import org.techconnect.model.User;
import org.techconnect.sql.TCDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
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
        final List<ImageButton> row_buttons = new ArrayList<ImageButton>(); //Store reference of where buttons are

        for (int i = 0; i < user.getExpertises().size(); i++) {
            TableRow toAdd = (TableRow) getLayoutInflater().inflate(R.layout.skill_tablerow,null,false);
            final ImageButton row_button = (ImageButton) toAdd.findViewById(R.id.skill_icon);
            row_buttons.add(row_button);
            row_button.setTag(i); //View that the button belongs to
            row_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //We want to delete the entire row that it belongs to
                    row_buttons.indexOf(row_button);
                    skills_table.removeViewAt(row_buttons.indexOf(row_button));
                    row_buttons.remove(row_button);
                }
            });
            TextInputLayout addSkill = (TextInputLayout) toAdd.findViewById(R.id.edit_skill_layout);
            addSkill.setVisibility(View.GONE);
            TextView toAddText = (TextView)  toAdd.findViewById(R.id.skill_text);
            toAddText.setText(user.getExpertises().get(i));
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


        final ImageButton editSkill = (ImageButton) findViewById(R.id.edit_skill_button);
        editSkill.setOnClickListener(new View.OnClickListener() {
            boolean isAdding = false;
            TableRow toAdd;
            TextInputLayout inputLayout;
            ImageButton icon;
            EditText add_skill;
            TextView skill_text;
            @Override
            public void onClick(View view) {
                //Add a new row to the list of skills or deleting old skills
                if (!isAdding) {
                    //Turn on all imagebuttons
                    for (ImageButton button : row_buttons) {
                        button.setClickable(true);
                        button.setImageResource(R.drawable.ic_close_black_24dp);
                    }
                    //Activate a potential new row
                    toAdd = (TableRow) getLayoutInflater().inflate(R.layout.skill_tablerow,null,false);
                    inputLayout = (TextInputLayout) toAdd.findViewById(R.id.edit_skill_layout);
                    icon = (ImageButton) toAdd.findViewById(R.id.skill_icon);
                    icon.setImageResource(R.drawable.ic_add_box_black_24dp);
                    add_skill = (EditText) toAdd.findViewById(R.id.edit_skill_text);
                    skill_text = (TextView) toAdd.findViewById(R.id.skill_text);

                    icon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //User actually entered something
                            if (add_skill.getText().length() > 0) {
                                skill_text.setText(add_skill.getText());
                                icon.setImageResource(R.drawable.ic_build_black_24dp);
                                skill_text.setVisibility(View.VISIBLE);
                                inputLayout.setVisibility(View.GONE);
                                row_buttons.add(icon);
                            }
                        }
                    });

                    editSkill.setImageResource(R.drawable.ic_done_black_24dp);
                    skills_table.addView(toAdd);
                } else { //Stopping adding
                    if (skill_text.getVisibility() != View.VISIBLE) {
                        skills_table.removeView(toAdd);
                    }
                    for (ImageButton button: row_buttons) {
                        button.setImageResource(R.drawable.ic_build_black_24dp);
                        button.setClickable(false);
                    }
                    editSkill.setImageResource(R.drawable.ic_mode_edit_black_24dp);
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
