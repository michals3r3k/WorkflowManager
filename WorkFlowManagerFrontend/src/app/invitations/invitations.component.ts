import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-invitations',
  templateUrl: './invitations.component.html',
  styleUrls: ['./invitations.component.css']
})
export class InvitationsComponent implements OnInit {

  constructor() { }

  Invitations = [
    {
      From: "TEST1",
      Subject: "Testowe zaproszenie",
      Content:  "fidouash yuigdifu ygsuiyfg uisdygf uisydgf uiysdgfui ysgdfui ysgdfiuy gsdiuyfg isudy gfiusgfisuy dgfuidy"
    },
    {
      From: "TEST2",
      Subject: "Testowe zaproszenie asdausdgiuaysgdiuyasgduiyagsiuddhausoiduha8isyugduiyasgduiyasgdui",
      Content:  "fidouash yuigdifu ygsuiyfg uisdygf uisydgf uiysdgfui ysgdfui ysgdfiuy gsdiuyfg isudy gfiusgfisuy dgfuidy"
    },
    {
      From: "TEST3",
      Subject: "Testowe zaproszenie",
      Content:  "fidouash yuigdifu ygsuiyfg uisdygf uisydgf uiysdgfui ysgdfui ysgdfiuy gsdiuyfg isudy gfiusgfisuy dgfuidy"
    },
    {
      From: "TEST4",
      Subject: "Testowe zaproszenie",
      Content:  "fidouash yuigdifu ygsuiyfg uisdygf uisydgf uiysdgfui ysgdfui ysgdfiuy gsdiuyfg isudy gfiusgfisuy dgfuidy"
    }
  ]

  ngOnInit() {
  }

}
