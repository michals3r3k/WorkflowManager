import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatToolbarModule } from '@angular/material/toolbar'; 
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list'
import { MatButtonModule } from '@angular/material/button'
import { MatIconModule } from "@angular/material/icon";
import { MatDialogModule } from "@angular/material/dialog";
import { MatCardModule } from "@angular/material/card";
import { HttpClientModule } from '@angular/common/http';
import { MatInputModule } from '@angular/material/input';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './login/login.component';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { SidenavComponent } from './sidenav/sidenav.component';
import { HomeComponent } from './home/home.component';
import { MenuItemComponent } from './sidenav/menu-item/menu-item.component';
import { WelcomeComponent } from './welcome/welcome.component';
import { AboutUsComponent } from './about-us/about-us.component';
import { OrganizationsComponent } from './organizations/organizations.component';
import { OrganizationDetailsComponent } from './organization-details/organization-details.component';
import { FilterPipe } from './Pipes/filter.pipe';
import { ProfileComponent } from './profile/profile.component';

@NgModule({
  declarations: [				
    AppComponent,
    LoginComponent,
    SidenavComponent,
    HomeComponent,
    MenuItemComponent,
    WelcomeComponent,
    AboutUsComponent,
    OrganizationsComponent,
      OrganizationDetailsComponent,
      FilterPipe,
      ProfileComponent
   ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    BrowserAnimationsModule,
    MatToolbarModule,
    MatSidenavModule,
    MatListModule,
    MatButtonModule,
    MatIconModule,
    MatDialogModule,
    MatCardModule,
    HttpClientModule,
    MatInputModule
  ],
  providers: [
    provideAnimationsAsync()
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
