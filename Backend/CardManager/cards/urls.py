from django.conf.urls import patterns, url

from cards import views

urlpatterns = patterns('',
    # ex: /cards/
    url(r'^$', views.index, name='index'),
    # ex: /cards/5/

    url(r'^(?P<card_id>\d+)/$', views.getCard, name='getCard'),

)