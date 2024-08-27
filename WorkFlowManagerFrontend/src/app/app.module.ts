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
import { MatSlideToggleModule } from '@angular/material/slide-toggle'; 
import { MatCheckboxModule } from '@angular/material/checkbox'
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatBadgeModule } from '@angular/material/badge'; 
import { MatMenuModule} from '@angular/material/menu'
import { MatTooltipModule } from '@angular/material/tooltip'; 
import { MatDatepickerModule } from '@angular/material/datepicker'; 
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
import { NewlinePipe } from './Pipes/newline.pipe';
import { TimeDiffToCurrentPipe } from './Pipes/timeDiffToCurrent.pipe';
import { OrganizationMemberPickerComponent } from './organizations/organization-member-picker/organization-member-picker.component';
import { RoleCreateComponent } from './organizations/role-create/role-create.component';
import { RoleSettingsComponent } from './organizations/role-settings/role-settings.component';
import { ProjectsComponent } from './organizations/projects/projects.component';
import { ProjectCreateComponent } from './organizations/projects/project-create/project-create.component';
import { ProjectDetailsComponent } from './organizations/projects/project-details/project-details.component';
import { OrganizationAddComponent } from './organizations/projects/organization-add/organization-add.component';
import { InvitationsComponent } from './invitations/invitations.component';
import { OrdersListComponent } from './orders-list/orders-list.component';
import { OrderListCardComponent } from './orders-list/order-list-card.component';
import { OrderComponent } from './order/order.component';
import { IssueListComponent } from './organizations/projects/issue/issue-list/issue-list.component';
import { IssueDetailsComponent } from './organizations/projects/issue/issue-details/issue-details.component';
import { FieldDefinitionEditComponent } from './order/field-definition-edit/field-definition-edit.component';
import { ChatComponent } from './chat/chat.component';
import { TaskDetailsComponent } from './organizations/projects/task-details/task-details.component';
import { OrderCreateComponent } from './orders-list/order-create/order-create.component';
import { OrganizationFilteringSelectComponent } from './components/organization-filtering-select/organization-filtering-select.component';
import { MatNativeDateModule } from '@angular/material/core';
import { OrganizationIssueDialogComponent } from './orders-list/organization-issue-dialog/organization-issue-dialog.component';
import { IssueFieldComponent } from './orders-list/issue-field/issue-field.component';
import { IssueFormComponent } from './orders-list/issue-form/issue-form.component';
import { MemberDeleteButton } from './organizations/organization-details/member-delete-button-component';
import { AddTaskComponent } from './organizations/projects/add-task/add-task.component';
import { DeleteGroupConfirmComponent } from './organizations/projects/delete-group-confirm/delete-group-confirm.component';
import { AddStatusComponent } from './organizations/projects/add-status/add-status.component';

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
    MemberDeleteButton,
    ResultToasterComponent,
    AboutUsComponent,
    ProfileComponent,
    FilterPipe,
    NewlinePipe,
    TimeDiffToCurrentPipe,
    OrganizationMemberPickerComponent,
    RoleCreateComponent,
    RoleSettingsComponent,
    ProjectsComponent,
    ProjectCreateComponent,
    ProjectDetailsComponent,
    OrganizationAddComponent,
    InvitationsComponent,
    OrdersListComponent,
    OrderListCardComponent,
    OrderComponent,
    IssueListComponent,
    IssueDetailsComponent,
    FieldDefinitionEditComponent,
    ChatComponent,
    OrderCreateComponent,
    OrganizationFilteringSelectComponent,
    OrganizationIssueDialogComponent,
    IssueFieldComponent,
    IssueFormComponent,
    TaskDetailsComponent,
    AddTaskComponent,
    DeleteGroupConfirmComponent,
    AddStatusComponent
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
    MatSlideToggleModule,
    MatCheckboxModule,
    MatAutocompleteModule,
    MatBadgeModule,
    MatMenuModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatTooltipModule,
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
