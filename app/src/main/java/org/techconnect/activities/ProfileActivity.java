package org.techconnect.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.centum.techconnect.R;
import org.techconnect.asynctasks.UpdateUserAsyncTask;
import org.techconnect.misc.auth.AuthManager;
import org.techconnect.model.User;
import org.techconnect.sql.TCDatabaseHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProfileActivity extends AppCompatActivity {
    //Do all of the butterknife binding
    @Bind(R.id.toolbar_layout)
    CollapsingToolbarLayout layout;
    @Bind(R.id.profile_work)
    TextView org;
    @Bind(R.id.profile_email)
    TextView email;
    @Bind(R.id.skills_table)
    TableLayout skills_table;

    //All of the editable text fields
    @Bind(R.id.edit_work_layout)
    TextInputLayout edit_org_layout;
    @Bind(R.id.edit_email_layout)
    TextInputLayout edit_email_layout;
    @Bind(R.id.edit_work_text)
    EditText edit_org;
    @Bind(R.id.edit_email_text)
    EditText edit_email;

    //All of the edit buttons
    @Bind(R.id.edit_work_button)
    ImageButton editWork;
    @Bind(R.id.edit_skill_button)
    ImageButton editSkill;
    @Bind(R.id.save_button)
    Button saveButton;
    @Bind(R.id.discard_button)
    Button discardButton;

    List<ImageButton> row_buttons;
    User head_user; //In cases without editing, this is only user needed
    User temp_user; //In cases with editing, need temporary user to store changes until committed

    List<String> tmp_skills; //Hold onto the actual final set of skills for the user
    boolean isEditable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        //Here, we access the current User from the Database, create a temporary user in case we need to update
        head_user = getIntent().getExtras().getParcelable("user");

        //Setup whether the user can edit this profile
        isEditable = AuthManager.get(this).hasAuth() && head_user.get_id().equals(AuthManager.get(this).getAuth().getUserId());


        //Only setup the temp user and skills in case where user is actual user
        if (isEditable) {
            tmp_skills = new ArrayList<String>();
            try {
                temp_user = head_user.clone();
            } catch (CloneNotSupportedException e) {
                Log.e("Profile", e.getMessage());
            }
        }

        //Add return arrow to action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Setup the Toolbar Title
        layout.setTitle(head_user.getName());

        //Setup the UI with the head_user information
        setupProfile();

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

    private TableRow onRowAddRequest() {
        final TableRow toAdd;
        final TextInputLayout inputLayout;
        final ImageButton icon;
        final EditText add_skill;
        final TextView skill_text;

        toAdd = (TableRow) getLayoutInflater().inflate(R.layout.tablerow_skill,null,false);
        inputLayout = (TextInputLayout) toAdd.findViewById(R.id.edit_skill_layout);
        icon = (ImageButton) toAdd.findViewById(R.id.skill_icon);
        icon.setImageResource(R.drawable.ic_add_box_black_24dp);
        add_skill = (EditText) toAdd.findViewById(R.id.edit_skill_text);
        skill_text = (TextView) toAdd.findViewById(R.id.skill_text);

        icon.setOnClickListener(new View.OnClickListener() {
            boolean adding = true; //Initially, adding a new row
            @Override
            public void onClick(View view) {
                //User actually entered something
                if (adding) {
                    if (add_skill.getText().length() > 0) {
                        skill_text.setText(add_skill.getText());
                        tmp_skills.add(add_skill.getText().toString());//Add to temp user
                        icon.setImageResource(R.drawable.ic_close_black_24dp);
                        skill_text.setVisibility(View.VISIBLE);
                        inputLayout.setVisibility(View.GONE);
                        onRowAddRequest();
                        adding = false;
                    }
                } else {
                    //We want to delete the entire row that it belongs to
                    skills_table.removeViewAt(row_buttons.indexOf(icon));
                    tmp_skills.remove(row_buttons.indexOf(icon));//Remove expertise
                    row_buttons.remove(icon);
                }
            }
        });

        row_buttons.add(icon);
        skills_table.addView(toAdd);
        return toAdd;
    }

    public void writeUserToDatabase(View v) throws ExecutionException, InterruptedException {
        //Use the temp_user object to write any user changes to the database
        final Context context = this;
        temp_user.setExpertises(tmp_skills);
        new UpdateUserAsyncTask(context) {
            ProgressDialog pd;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pd = ProgressDialog.show(ProfileActivity.this, getString(R.string.save_changes), null, true, false);
            }

            @Override
            protected void onPostExecute(User u) {
                pd.dismiss();
                pd = null;
                if (u != null) {
                    TCDatabaseHelper.get(context).upsertUser(u);

                } else {
                    new AlertDialog.Builder(context)
                            .setTitle(R.string.error)
                            .setMessage(R.string.failed_update_user)
                            .show();

                }
            }
        }.execute(temp_user);
        //Reset the head user to be the newly edited version, which is now stored in the database
        head_user = TCDatabaseHelper.get(context).getUser(temp_user.get_id());

        v.setVisibility(View.GONE);
        discardButton.setVisibility(View.GONE);
    }

    public void discardUserChanges(View v) {
        //Want to restore the original user (head_user)
        skills_table.removeAllViews(); //Clear out all previous rows
        setupProfile();
        try {
            temp_user = head_user.clone();
        } catch(CloneNotSupportedException e) {
            Log.e("Profile",e.getMessage());
        }

        v.setVisibility(View.GONE);
        saveButton.setVisibility(View.GONE);
    }


    private void setupProfile() {
        //Add organization, email, and skills to the list below
        org.setText(head_user.getOrganization());
        email.setText(head_user.getEmail());

        //Create all rows from list of skills in profile
        row_buttons = new ArrayList<ImageButton>(); //Store reference of where buttons are

        for (int i = 0; i < head_user.getExpertises().size(); i++) {
            TableRow toAdd = (TableRow) getLayoutInflater().inflate(R.layout.tablerow_skill,null,false);
            final ImageButton row_button = (ImageButton) toAdd.findViewById(R.id.skill_icon);
            row_buttons.add(row_button);
            row_button.setTag(i); //View that the button belongs to

            //Don't know if I should set the click listener every time, but doing it for now
            row_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //We want to delete the entire row that it belongs to
                    skills_table.removeViewAt(row_buttons.indexOf(row_button));
                    tmp_skills.remove(row_buttons.indexOf(row_button));
                    row_buttons.remove(row_button);
                }
            });
            row_button.setClickable(false);

            TextInputLayout addSkill = (TextInputLayout) toAdd.findViewById(R.id.edit_skill_layout);
            addSkill.setVisibility(View.GONE);
            TextView toAddText = (TextView)  toAdd.findViewById(R.id.skill_text);
            toAddText.setText(head_user.getExpertises().get(i));
            toAddText.setVisibility(View.VISIBLE);

            if (isEditable) { //Only need tmp_skills if editing
                tmp_skills.add(head_user.getExpertises().get(i));
            }

            skills_table.addView(toAdd);
        }

        //Register the edit text fields for changing account info
        edit_org.setText(head_user.getOrganization());
        edit_email.setText(head_user.getEmail());

        //Setup the edit button listeners to make changes to the profile info
        if (isEditable) {
            setUpEditButtons();
        }

    }

    private void setUpEditButtons() {
        //Setup the edit button listeners to make changes to the profile info
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
                    head_user.setOrganization(edit_org.getText().toString());//Update Reference
                    temp_user.setEmail(edit_email.getText().toString());

                    edit_org_layout.setVisibility(View.GONE);
                    edit_email_layout.setVisibility(View.GONE);
                    editWork.setImageResource(R.drawable.ic_mode_edit_black_24dp);
                    saveButton.setVisibility(View.VISIBLE);
                    discardButton.setVisibility(View.VISIBLE);
                }
                isEditing = !isEditing;
            }
        });
        editWork.setClickable(isEditable);

        editSkill.setOnClickListener(new View.OnClickListener() {
            boolean isAdding = false;
            @Override
            public void onClick(View view) {
                //Add a new row to the list of skills or deleting old skills
                if (!isAdding) {
                    for (ImageButton button : row_buttons) {
                        button.setClickable(true);
                        button.setImageResource(R.drawable.ic_close_black_24dp);
                    }
                    onRowAddRequest();
                    editSkill.setImageResource(R.drawable.ic_done_black_24dp);
                } else { //Stopping adding
                    skills_table.removeViewAt(skills_table.getChildCount() -1);//Always delete the last one
                    row_buttons.remove(row_buttons.size() - 1); //Remove the last one
                    for (ImageButton button: row_buttons) {
                        button.setImageResource(R.drawable.ic_build_black_24dp);
                        button.setClickable(false);
                    }
                    editSkill.setImageResource(R.drawable.ic_mode_edit_black_24dp);
                    saveButton.setVisibility(View.VISIBLE);
                    discardButton.setVisibility(View.VISIBLE);
                }
                isAdding = !isAdding;
            }
        });
        editSkill.setClickable(isEditable);

    }


}
