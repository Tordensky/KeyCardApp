from django.conf.urls import patterns, url

from login import views

urlpatterns = patterns('',
   url(r'^login', views.loginToServer, name='loginToServer'),
   url(r'^new', views.newUser, name='newUser'),
   url(r'^logout', views.logoutFromServer, name='logoutFromServer'),
)