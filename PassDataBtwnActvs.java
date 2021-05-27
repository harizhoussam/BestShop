package com.example.ourapplication;

import android.app.Application;

public class PassDataBtwnActvs extends Application {

        private String username, longitude, latitude, search, origin;


        public String getUsername() { return username; }

        public void setUsername(String username) {
            this.username = username;
        }


        public String getLongitude() {
        return longitude;
    }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }


        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
        this.latitude = latitude;
    }


        public String getSearch() {
            return search;
        }

        public void setSearch(String search) {
        this.search = search;
    }


        public String getOrigin() {
            return origin;
        }

        public void setOrigin(String origin) {
            this.origin = origin;
        }
    }

