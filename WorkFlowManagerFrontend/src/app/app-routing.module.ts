import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { WelcomeComponent } from './welcome/welcome.component';
import { AboutUsComponent } from './about-us/about-us.component';
import { OrganizationsComponent } from './organizations/organizations.component';
import { OrganizationDetailsComponent } from './organizations/organization-details/organization-details.component';
import { ProfileComponent } from './profile/profile.component';
import { ProjectDetailsComponent } from './organizations/projects/project-details/project-details.component';
import { InvitationsComponent } from './invitations/invitations.component';
import { OrdersListComponent } from './orders-list/orders-list.component';
import { OrderComponent } from './order/order.component';
import { IncommingIssueListComponent } from './organizations/projects/issue/issue-list/incomming-issue-list.component';
import { ChatComponent } from './chat/chat.component';
import { OutgoingIssueListComponent } from './organizations/projects/issue/outgoing-issue-list/outgoing-issue-list.component';

const routes: Routes = [
  {
    path: '',
    redirectTo: 'welcome',
    pathMatch: 'full'
  },
  {
    path: 'home',
    component: HomeComponent
  },
  {
    path: 'welcome',
    component: WelcomeComponent
  },
  {
    path: 'about-us',
    component: AboutUsComponent
  },
  {
    path: 'organizations',
    component: OrganizationsComponent
  },
  {
    path: 'organization-details/:id',
    component: OrganizationDetailsComponent
  },
  {
    path: 'project-details/:organizationId/:projectId',
    component: ProjectDetailsComponent
  },
  {
    path: 'project-details/:organizationId/:projectId/:taskId',
    component: ProjectDetailsComponent
  },
  {
    path: 'profile',
    component: ProfileComponent
  },
  {
    path: 'invitations',
    component: InvitationsComponent
  },
  {
    path: 'orders',
    component: OrdersListComponent
  },
  {
    path: 'order/:organizationId',
    component: OrderComponent
  },
  {
    path: 'incomming-issues/:organizationId/:projectId',
    component: IncommingIssueListComponent
  },
  {
    path: 'incomming-issues/:organizationId/:projectId/:taskId',
    component: IncommingIssueListComponent
  },
  {
    path: 'outgoing-issues/:organizationId/:projectId',
    component: OutgoingIssueListComponent
  },
  {
    path: 'outgoing-issues/:organizationId/:projectId/:taskId',
    component: OutgoingIssueListComponent
  },
  {
    path: 'chat',
    component: ChatComponent
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
