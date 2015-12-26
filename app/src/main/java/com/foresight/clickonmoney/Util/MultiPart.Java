 class MultiPart extends AsyncTask<Void, Void, String> {

        private final CustomProgressDialog dialog = CustomProgressDialog.createProgressBar(RegisterActivity.this);
        InputStream is;
        String sResponse;
        boolean req_as_member;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(Void... unsued) {
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpContext localContext = new BasicHttpContext();
                HttpPost httpPost = new HttpPost(Constants.api_v1 + Constants.api_register);

                // httpPost.setHeader("Content-Type","undefined");

                MultipartEntity entity = new MultipartEntity(
                        HttpMultipartMode.BROWSER_COMPATIBLE);
                JSONArray jsonArray = new JSONArray();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("first_name", first_name);
                jsonObject.put("last_name", last_name);
                jsonObject.put("mobile", mobile);
                jsonObject.put("line1", line1);
                jsonObject.put("line2", line2);
                jsonObject.put("city", city);
                jsonObject.put("state", state);
                jsonObject.put("country", country);
                jsonObject.put("pincode", pincode);
                JSONArray array = new JSONArray();
                array.put(jsonObject);
                entity.addPart("first_name", new StringBody(first_name));
                entity.addPart("last_name", new StringBody(last_name));
                entity.addPart("email", new StringBody(email));
                entity.addPart("mobile", new StringBody(mobile));
                entity.addPart("password", new StringBody(password));
                entity.addPart("address_book", new StringBody(array.toString()));
                entity.addPart("is_newsletter", new StringBody(radiobuttonYes.isChecked() + ""));
                if (isProfileSelected) {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                    byte[] data = bos.toByteArray();
                    entity.addPart("image", new ByteArrayBody(data,
                            "myImage.jpg"));
                } else {
                    //  entity.addPart("photo", new StringBody("false"));
                }

                httpPost.setEntity(entity);
                HttpResponse response = httpClient.execute(httpPost,
                        localContext);

                try {
                    is = response.getEntity().getContent();
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(is, "iso-8859-1"), 8);
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    is.close();
                    sResponse = sb.toString();

                } catch (Exception e) {
                    Log.e("Buffer Error",
                            "Error converting result " + e.toString());
                }
                return sResponse;
            } catch (Exception e) {
                // Toast.makeText(getApplicationContext(), "Error",
                // Toast.LENGTH_LONG).show();
                Log.e("Do in back" + e.getClass().getName(), e.getMessage(), e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String sResponse) {
            try {
                if (dialog.isShowing())
                    dialog.dismiss();

                if (sResponse != null) {
                    Log.d("Json", sResponse);
                    JSONObject jObj = new JSONObject(sResponse);

                    boolean flag = false;
                    flag = jObj.has("flag") ? jObj.getBoolean("flag") : false;
                    String message = jObj.has("message") ? jObj
                            .getString("message") : "";
                    if (flag) {

                        Toast.makeText(RegisterActivity.this,
                                "Successfully Registered", Toast.LENGTH_LONG)
                                .show();
                        JSONObject dataObj = jObj.getJSONObject("data");
                        Log.d("data", dataObj.toString());
                        UserDataPreferences.saveToken(RegisterActivity.this, JSONData.getString(dataObj, "token"));
                        UserDataPreferences.saveUserInfo(RegisterActivity.this, dataObj.toString());
                        Intent mainActivity = new Intent(RegisterActivity.this,
                                MainActivity.class);
                        mainActivity.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        mainActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainActivity);
                        overridePendingTransition(R.anim.animation, R.anim.animation2);
                        closeActivity();
                    } else {
                        if (message.equals("")) {
                            AppConstant.showSingleButtonAlertDialog(
                                    RegisterActivity.this, "Error",
                                    "Something went wrong.\nPlease try again");
                        } else {
                            AppConstant.showSingleButtonAlertDialog(
                                    RegisterActivity.this, "Error", message);
                        }
                    }
                }
            } catch (Exception e) {
                AppConstant.showNetworkError(RegisterActivity.this);
                Log.e(e.getClass().getName(), e.getMessage(), e);
            }
        }
    }
