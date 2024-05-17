import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { WelcomeComponent } from './welcome/welcome.component';
import { AboutUsComponent } from './about-us/about-us.component';
import { OrganizationsComponent } from './organizations/organizations.component';
import { OrganizationDetailsComponent } from './organizations/organization-details/organization-details.component';
import { ProfileComponent } from './profile/profile.component';
import { ProjectDetailsComponent } from './organizations/projects/project-details/project-details.component';

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
    path: 'project-details/:id',
    component: ProjectDetailsComponent
  },
  {
    path: 'profile',
    component: ProfileComponent
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
