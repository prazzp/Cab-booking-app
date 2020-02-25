from django.conf.urls import url
from django.contrib import admin
from .views import (UserListView, UserUpdateView, UserCreateView,
                    CarListView, CarStatusUpdateView, TripListView,
                    TripUpdateView, TripCreateView, UserLogin,
                    TripListViewByUser, TripListViewByCar, CarActiveTrip)

urlpatterns = [
    # url(r'^login/', ),
    url(r'^users$', UserListView.as_view(), name='userlist'),
    url(r'^user/(?P<id>[\d]+)/edit', UserUpdateView.as_view(), name='userupdate'),
    url(r'^user/create', UserCreateView.as_view(), name='usercreate'),
    url(r'^cars/(?P<id>[\d]+)/update', CarStatusUpdateView.as_view(), name='carupdate'),
    url(r'^cars', CarListView.as_view(), name='carlist'),
    url(r'^trips$', TripListView.as_view(), name='triplist'),
    url(r'^trip/(?P<id>[\d]+)/update', TripUpdateView.as_view(), name='tripupdate'),
    url(r'^trip/create', TripCreateView.as_view(), name='tirpcreate'),
    url(r'login$', UserLogin, name='login'),
    url(r'trips/usertrips$', TripListViewByUser.as_view(), name="usertrips"),
    url(r'trips/cartrips$', TripListViewByCar.as_view(), name="cartrips"),
    url(r'trips/caractiveTrips$', CarActiveTrip.as_view(), name="caractivetrips")
]