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
import { MatCardModule } from "@angular/material/card"
import { MatInputModule } from '@angular/material/input';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatMenuModule} from '@angular/material/menu'
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http'
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
import { OrganizationCreateComponent } from './organizations/organization-create/organization-create.component';
import { OrganizationDetailsComponent } from './organization-details/organization-details.component';
import { ResultToasterComponent } from './result-toaster/result-toaster.component';
import { ProfileComponent } from './profile/profile.component';
import { Router } from '@angular/router';
import { AuthInterceptorService } from './services/auth-interceptor/auth-interceptor.service';

import { FilterPipe } from './Pipes/filter.pipe';

@NgModule({
  declarations: [				
    AppComponent,
    LoginComponent,
    SidenavComponent,
    HomeComponent,
    MenuItemComponent,
    WelcomeComponent,
    OrganizationsComponent,
    OrganizationCreateComponent,
    OrganizationDetailsComponent,
    ResultToasterComponent,
    AboutUsComponent,
    ProfileComponent,
    FilterPipe
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
    MatInputModule,
    MatSnackBarModule,
    MatMenuModule,
    HttpClientModule
  ],
  providers: [
    provideAnimationsAsync(),
    {
      provide: HTTP_INTERCEPTORS,
      useFactory: function(router: Router) {
        return new AuthInterceptorService(router);
      },
      multi: true,
      deps: [Router]
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
