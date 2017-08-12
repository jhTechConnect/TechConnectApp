package org.techconnect.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.techconnect.R;
import org.techconnect.model.User;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class EditProfileActivity extends AppCompatActivity {

    @Bind(R.id.workHeader)
    TextView workHeader;
    @Bind(R.id.profile_email)
    TextView profileEmail;
    @Bind(R.id.edit_work_text)
    EditText editWorkText;
    @Bind(R.id.delete_org)
    ImageButton deleteOrg;
    @Bind(R.id.skillHeader)
    TextView skillHeader;
    @Bind(R.id.skillsTable)
    TableLayout skillsTable;

    public static final String EXTRA_USER = "org.techconnect.editprofileactivity.user";

    //Intent requests
    public static int SKILL_REQUEST = 0;

    //Intent result codes
    public static int USER_SAME = 0;
    public static int USER_CHANGE = 1;

    private User head_user;
    private User temp_user;
    private List<ImageButton> row_buttons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        ButterKnife.bind(this);

        if (getIntent().hasExtra(EXTRA_USER)) {
            head_user = getIntent().getParcelableExtra(EXTRA_USER);
        } else {
            Log.e(getClass().toString(), "No user attached");
            finish();
        }

        setTitle("Edit Profile");

        //Setup the hints for edit text, email
        editWorkText.setText(head_user.getOrganization());
        profileEmail.setText(head_user.getEmail());

        //Setup the table of skills
        setupSkillsTable();

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    private void setupSkillsTable() {
        //Create all rows from list of skills in profile
        row_buttons = new ArrayList<ImageButton>();

        if (head_user.getExpertises().size() == 0) {
            //Current User lists no skills
            TableRow toAdd = (TableRow) getLayoutInflater().inflate(R.layout.tablerow_skill, null, false);
            ImageButton row_button = (ImageButton) toAdd.findViewById(R.id.delete_icon);
            TextView toAddText = (TextView) toAdd.findViewById(R.id.skill_text);

            //Make the button invisible
            row_button.setVisibility(View.INVISIBLE);
            row_button.setClickable(false);
            //Chance the text to be appropriate to having no skills
            toAddText.setText(R.string.no_skills);
            toAddText.setTextColor(Color.GRAY);
            toAddText.setVisibility(View.VISIBLE);
            //Still need to cancel out the edit text view
            TextInputLayout addSkill = (TextInputLayout) toAdd.findViewById(R.id.edit_skill_layout);
            addSkill.setVisibility(View.GONE);
            skillsTable.addView(toAdd);
        } else {
            for (int i = 0; i < head_user.getExpertises().size(); i++) {
                TableRow toAdd = (TableRow) getLayoutInflater().inflate(R.layout.tablerow_skill, null, false);
                final ImageButton row_button = (ImageButton) toAdd.findViewById(R.id.delete_icon);
                row_button.setVisibility(View.VISIBLE);
                row_buttons.add(row_button);
                row_button.setTag(i); //View that the button belongs to

                //Don't know if I should set the click listener every time, but doing it for now
                row_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //We want to delete the entire row that it belongs to
                        skillsTable.removeViewAt(row_buttons.indexOf(row_button));
                        row_buttons.remove(row_button);
                    }
                });
                row_button.setClickable(true);

                TextInputLayout addSkill = (TextInputLayout) toAdd.findViewById(R.id.edit_skill_layout);
                addSkill.setVisibility(View.VISIBLE);
                EditText addSkillText = (EditText) toAdd.findViewById(R.id.edit_skill_text);
                addSkillText.setText(head_user.getExpertises().get(i));
                skillsTable.addView(toAdd);
            }

            //Have the add skill view visible
            TableRow toAdd = (TableRow) getLayoutInflater().inflate(R.layout.tablerow_skill, null, false);
            final ImageButton icon = (ImageButton) toAdd.findViewById(R.id.delete_icon);
            icon.setVisibility(View.VISIBLE);
            icon.setImageResource(R.drawable.ic_add_box_black_24dp);
            /*
            icon.setOnClickListener(new View.OnClickListener() {
                boolean adding = true; //Initially, adding a new row

                @Override
                public void onClick(View view) {
                    if (adding) {
                        adding = false;
                        icon.setImageResource(R.drawable.ic_close_black_24dp);
                        //User wants to add something
                        Intent intent = new Intent(EditProfileActivity.this, SelectSkillActivity.class);
                        startActivityForResult(intent,SKILL_REQUEST);
                        onRowAddRequest();
                    } else {
                        //We want to delete the entire row that it belongs to
                        skillsTable.removeViewAt(row_buttons.indexOf(icon));
                        row_buttons.remove(icon);
                    }
                }
            });
            */

            toAdd.findViewById(R.id.skill_icon).setVisibility(View.INVISIBLE);

            EditText add_skill = (EditText) toAdd.findViewById(R.id.edit_skill_text);
            TextView skill_text = (TextView) toAdd.findViewById(R.id.skill_text);
            skill_text.setText("Add Skill");

            add_skill.setVisibility(View.GONE);
            skill_text.setVisibility(View.VISIBLE);

            row_buttons.add(icon);
            skillsTable.addView(toAdd);
        }
    }

    private TableRow onRowAddRequest() {
        final TableRow toAdd;
        final ImageButton icon;
        final EditText add_skill;
        final TextView skill_text;

        toAdd = (TableRow) getLayoutInflater().inflate(R.layout.tablerow_skill, null, false);
        icon = (ImageButton) toAdd.findViewById(R.id.delete_icon);
        icon.setImageResource(R.drawable.ic_add_box_black_24dp);
        add_skill = (EditText) toAdd.findViewById(R.id.edit_skill_text);
        skill_text = (TextView) toAdd.findViewById(R.id.skill_text);
        skill_text.setText("Add Skill");

        add_skill.setVisibility(View.GONE);
        skill_text.setVisibility(View.VISIBLE);

        icon.setOnClickListener(new View.OnClickListener() {
            boolean adding = true; //Initially, adding a new row

            @Override
            public void onClick(View view) {
                if (adding) {
                    adding = false;
                    icon.setImageResource(R.drawable.ic_close_black_24dp);
                    //User wants to add something
                    Intent intent = new Intent(EditProfileActivity.this, SelectSkillActivity.class);
                    startActivityForResult(intent,SKILL_REQUEST);
                    onRowAddRequest();
                } else {
                    //We want to delete the entire row that it belongs to
                    skillsTable.removeViewAt(row_buttons.indexOf(icon));
                    row_buttons.remove(icon);
                }
            }
        });

        //row_buttons.add(icon);
        skillsTable.addView(toAdd);
        return toAdd;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_editprofile_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        } else if (id == android.R.id.home) {
            if (isEdited()) {
                super.onBackPressed();
                return true;
            }
        } else if (id == R.id.acceptChanges) {
            saveChanges();
            Intent result = new Intent();
            result.putExtra(ProfileActivity.EXTRA_USER,head_user);
            setResult(USER_CHANGE,result);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Update the user stored in SQL to reflect changes made here
     */
    private void saveChanges() {
    }


    /**
     * Lame helper method to determine if any of the fields changed
     * @return
     */
    private boolean isEdited() {
        if (!head_user.getOrganization().equals(editWorkText.getText().toString())) {
            return true;
        } else {
            return false;
        }
    }

}
