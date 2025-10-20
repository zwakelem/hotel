import { Routes } from '@angular/router';
import { Register } from './userprofile/register/register';
import { Profile } from './userprofile/profile/profile';
import { Guard } from './service/guard';
import { EditProfile } from './userprofile/edit-profile/edit-profile';
import { Rooms } from './room/rooms/rooms';
import { Login } from './common/login/login';
import { Home } from './common/home/home';
import { RoomDetails } from './room/room-details/room-details';
import { PaymentComponent } from './payment/payment-component/payment-component';
import { PaymentSuccessComponent } from './payment/payment-success-component/payment-success-component';
import { PaymentFailureComponent } from './payment/payment-failure-component/payment-failure-component';
import { FindBooking } from './booking/find-booking/find-booking';

export const routes: Routes = [
  {
    path: 'login',
    component: Login,
  },
  {
    path: 'register',
    component: Register,
  },
  {
    path: 'home',
    component: Home,
  },

  {
    path: 'profile',
    component: Profile,
    canActivate: [Guard],
  },
  {
    path: 'edit-profile',
    component: EditProfile,
    canActivate: [Guard],
  },
  {
    path: 'rooms',
    component: Rooms,
  },
  {
    path: 'rooms-details/:roomId',
    component: RoomDetails,
    // canActivate: [Guard],
  },
  {
    path: 'find-booking',
    component: FindBooking,
  },
  {
    path: 'payment/:bookingReference/:amount',
    component: PaymentComponent,
    canActivate: [Guard],
  },
  {
    path: 'payment-success',
    component: PaymentSuccessComponent,
    canActivate: [Guard],
  },
  {
    path: 'payment-failure',
    component: PaymentFailureComponent,
    canActivate: [Guard],
  },
  {
    path: '**',
    redirectTo: 'home',
  },
];
