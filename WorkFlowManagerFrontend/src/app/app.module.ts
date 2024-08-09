import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list'
import { MatButtonModule } from '@angular/material/button'
import { MatExpansionModule } from '@angular/material/expansion'; 
import { MatIconModule } from "@angular/material/icon";
import { MatDialogModule } from "@angular/material/dialog";
import { MatCardModule } from "@angular/material/card"
import { MatChipsModule } from '@angular/material/chips';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatSelectModule } from '@angular/material/select';
import { MatCheckboxModule } from '@angular/material/checkbox'
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatMenuModule} from '@angular/material/menu'
import { DragDropModule } from '@angular/cdk/drag-drop';
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
import { OrganizationDetailsComponent } from './organizations/organization-details/organization-details.component';
import { ResultToasterComponent } from './result-toaster/result-toaster.component';
import { ProfileComponent } from './profile/profile.component';
import { Router } from '@angular/router';
import { AuthInterceptorService } from './services/auth-interceptor/auth-interceptor.service';

import { FilterPipe } from './Pipes/filter.pipe';
import { OrganizationMemberPickerComponent } from './organizations/organization-member-picker/organization-member-picker.component';
import { RoleCreateComponent } from './organizations/role-create/role-create.component';
import { RoleSettingsComponent } from './organizations/role-settings/role-settings.component';
import { ProjectsComponent } from './organizations/projects/projects.component';
import { ProjectCreateComponent } from './organizations/projects/project-create/project-create.component';
import { ProjectDetailsComponent } from './organizations/projects/project-details/project-details.component';
import { OrganizationAddComponent } from './organizations/projects/organization-add/organization-add.component';
import { InvitationsComponent } from './invitations/invitations.component';
import { OrdersListComponent } from './orders-list/orders-list.component';
import { OrderComponent } from './order/order.component';
import { IssueListComponent } from './organizations/projects/issue/issue-list/issue-list.component';

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
    FilterPipe,
    OrganizationMemberPickerComponent,
    RoleCreateComponent,
    RoleSettingsComponent,
    ProjectsComponent,
    ProjectCreateComponent,
    ProjectDetailsComponent,
    OrganizationAddComponent,
      InvitationsComponent,
      OrdersListComponent,
      OrderComponent,
      IssueListComponent
   ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    ReactiveFormsModule,
    BrowserAnimationsModule,
    MatToolbarModule,
    MatSidenavModule,
    MatListModule,
    MatButtonModule,
    MatExpansionModule,
    MatIconModule,
    MatDialogModule,
    MatCardModule,
    MatChipsModule,
    MatInputModule,
    MatSnackBarModule,
    MatSelectModule,
    MatCheckboxModule,
    MatAutocompleteModule,
    MatMenuModule,
    DragDropModule,
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
