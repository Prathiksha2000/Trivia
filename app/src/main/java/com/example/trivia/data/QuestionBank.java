package com.example.trivia.data;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.trivia.controller.AppController;
import com.example.trivia.model.Question;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;


public class QuestionBank {
    ArrayList<Question> questionArrayList =new ArrayList<>();

    //http link from where we get questions in json format

    private  String url="https://raw.githubusercontent.com/curiousily/simple-quiz/master/script/statements-data.json";

    //questions are obtained from json format http request using JsonArrayRequest and stored in a arrayList

    public List<Question>getQuestions(final AnswerListAsyncResponse callBack){
        //Log.d("jsonessss","working fine");
        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(
                Request.Method.GET,
                url,
                (JSONArray) null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        for (int i = 0; i < response.length(); i++) {

                            try
                            {
                                Question question = new Question();
                                question.setAnswer(response.getJSONArray(i).get(0).toString());
                                question.setAnswerTrue(response.getJSONArray(i).getBoolean(1));


                                questionArrayList.add(question);
                                Log.d("JSON1", "OnResponse:" + question);

                                // Log.d("JSON","OnResponse:"+ response.getJSONArray(i).get(0)) ;
                           } catch (JSONException e) {
                                //  Log.d("JSON2","OnResponse:"+ response.getJSONArray(i).getBoolean(1)) ;
                                e.printStackTrace();
                            }

                        }
                        if (null != callBack)
                            callBack.processFinished(questionArrayList);


                     // Log.d("JSON Stuff","OnResponse:"+response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("errorreeeeeee",error.getMessage().toString());
            }
        }
        );
        AppController.getInstance().addToRequestQueue(jsonArrayRequest);
        return  questionArrayList;
    }
}

