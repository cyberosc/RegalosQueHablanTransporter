package com.acktos.regalosquehablan.transporter.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Acktos on 10/15/15.
 */
public class Transporter extends BaseModel {

    private String id;
    private String name;
    private String lastName;
    private String email;
    private String phone;
    private String cc;

    public static final String KEY_NAME="name";
    public static final String KEY_LAST_NAME="last_name";
    public static final String KEY_EMAIL="email";
    public static final String KEY_PHONE="phone";
    public static final String KEY_CC="cc";
    public static final String KEY_TRANSPORTER_ID="transporter_id";

    public Transporter(){}

    public Transporter(JSONObject jsonObject){
        try{
            if(jsonObject.has(KEY_ID)) {
                setId(jsonObject.getString(KEY_ID));
            }
            if(jsonObject.has(KEY_NAME)) {
                setName(jsonObject.getString(KEY_NAME));
            }
            if(jsonObject.has(KEY_LAST_NAME)) {
                setLastName(jsonObject.getString(KEY_LAST_NAME));
            }
            if(jsonObject.has(KEY_EMAIL)) {
                setEmail(jsonObject.getString(KEY_EMAIL));
            }
            if(jsonObject.has(KEY_PHONE)) {
                setPhone(jsonObject.getString(KEY_PHONE));
            }
            if(jsonObject.has(KEY_CC)) {
                setCc(jsonObject.getString(KEY_CC));
            }

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    public String toString(){
        JSONObject jsonObject=new JSONObject();

        try{
            if(getId()!=null){
                jsonObject.put(KEY_ID,getId());
            }
            if(getName()!=null){
                jsonObject.put(KEY_NAME,getName());
            }
            if(getLastName()!=null){
                jsonObject.put(KEY_LAST_NAME,getLastName());
            }
            if(getEmail()!=null){
                jsonObject.put(KEY_EMAIL,getEmail());
            }
            if(getPhone()!=null){
                jsonObject.put(KEY_PHONE,getPhone());
            }
            if(getCc()!=null){
                jsonObject.put(KEY_CC,getCc());
            }

        }catch (JSONException e){
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }
}
