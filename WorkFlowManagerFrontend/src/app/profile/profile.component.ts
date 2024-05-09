import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {

  editMode: boolean = false;

  organizations = [
    {
      name: "organization 1",
      role: "Admin",
      routeLink: "http://localhost:4200/organization-details"
    },
    {
      name: "organization 2",
      role: "Programist",
      routeLink: "http://localhost:4200/organization-details"
    }
  ];

  projects = [
    {
      name: "project 1",
      role: "Admin",
      routeLink: "http://localhost:4200"
    },
    {
      name: "project 2",
      role: "Programist",
      routeLink: "http://localhost:4200"
    },
    {
      name: "project 3",
      role: "Programist",
      routeLink: "http://localhost:4200"
    }
  ];

  tasks = [
    {
      name: "task 1",
      role: "Admin",
      routeLink: "http://localhost:4200"
    },
    {
      name: "task 2",
      role: "Programist",
      routeLink: "http://localhost:4200"
    },
    {
      name: "task 3",
      role: "Programist",
      routeLink: "http://localhost:4200"
    }
  ];

  constructor() { }

  ngOnInit() {
  }

  toggleEditMode() {
    this.editMode = !this.editMode;
  }

}
