import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-organization-details',
  templateUrl: './organization-details.component.html',
  styleUrls: ['./organization-details.component.css']
})
export class OrganizationDetailsComponent implements OnInit {

  searchUser = "";

  users = [
    {
      name: "Hubert Rogowski",
      role: "Admin"
    },
    {
      name: "Michał Serek",
      role: "Admin"
    },
    {
      name: "Adam Orzeł",
      role: "Moderator"
    },
    {
      name: "Mateusz Nowak",
      role: "Programist"
    }
  ];
  roles = [
    {
      name: "Admin",
      users: 2
    },
    {
      name: "PM",
      users: 3
    },
    {
      name: "Programist",
      users: 8
    },
    {
      name: "QA",
      role: 2
    }
  ];

  constructor() { }

  ngOnInit() {
  }

}
