import { Routes } from '@angular/router';
import { AdminHomeComponent } from './admin/admin-home-component/admin-home-component';
import { ManageRoomsComponent } from './admin/manage-rooms-component/manage-rooms-component';
import { FindBooking } from './booking/find-booking/find-booking';
import { Home } from './common/home/home';
import { Login } from './common/login/login';
import { PaymentComponent } from './payment/payment-component/payment-component';
import { PaymentFailureComponent } from './payment/payment-failure-component/payment-failure-component';
import { PaymentSuccessComponent } from './payment/payment-success-component/payment-success-component';
import { RoomDetails } from './room/room-details/room-details';
import { Rooms } from './room/rooms/rooms';
import { Guard } from './service/guard';
import { EditProfile } from './userprofile/edit-profile/edit-profile';
import { Profile } from './userprofile/profile/profile';
import { Register } from './userprofile/register/register';

export const routes: Routes = [

  {path: 'login', component: Login},
  {path: 'register',component: Register},
  {path: 'home', component: Home},

  {path: 'profile', component: Profile, canActivate: [Guard]},
  { path: 'edit-profile', component: EditProfile, canActivate: [Guard] },
  
  {path: 'rooms', component: Rooms},
  {path: 'rooms-details/:roomId', component: RoomDetails, canActivate: [Guard]},
  {path: 'find-booking', component: FindBooking },
  
  {path: 'payment/:bookingReference/:amount', component: PaymentComponent, canActivate: [Guard]},
  {path: 'payment-success/:bookingReference', component: PaymentSuccessComponent, canActivate: [Guard]},
  { path: 'payment-failure', component: PaymentFailureComponent, canActivate: [Guard] },
  
  { path: 'payment-failure', component: PaymentFailureComponent, canActivate: [Guard] },
  
  {path: 'admin', component: AdminHomeComponent, canActivate: [Guard], data: {requiresAdmin: true} },
  {path: 'admin/manage-rooms', component: ManageRoomsComponent, canActivate: [Guard], data: {requiresAdmin: true} },
  
  {path: '**', redirectTo: 'home'},
];
