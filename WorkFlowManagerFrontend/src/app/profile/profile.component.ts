import { Component, OnInit } from '@angular/core';
import { SafeUrl } from '@angular/platform-browser';
import { ProfileEdit, ProfileService } from '../services/profile/profile.service';
import { Observable } from 'rxjs';
import { ServiceResultHelper } from '../services/utils/service-result-helper';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {

  editMode: boolean = false;
  imageUrl$: Observable<SafeUrl | null>;
  editData$: Observable<ProfileEdit>;
  newFile: FormData | null = null;

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

  constructor(
    private service: ProfileService,
    private serviceResultHelper: ServiceResultHelper
  ) { }

  ngOnInit() {
    this._loadImg();
    this._loadUserData();
  }

  _loadUserData() {
    this.editData$ = this.service.getData();
  }

  _loadImg() {
    this.imageUrl$ = this.service.getImg();
  }

  toggleEditMode() {
    this.editMode = !this.editMode;
  }

  openImgUpload(event: Event) {
    const btn: HTMLElement = event.currentTarget as HTMLElement;
    const uploader = btn.querySelector("input[type='file']") as HTMLInputElement;
    if(uploader) {
      uploader.click();
    }
  }

  uploadImg(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const file = input.files[0];
      if(file.type.startsWith('image/')) {
        this.newFile = new FormData();
        this.newFile.append("file", file);
        this.imageUrl$ = this.service.uploadImg(this.newFile);
      }
      else {
        alert('Please select image file')
      }
    }
  }

  save(editData: ProfileEdit) {
    this.service.saveData(editData).subscribe(result => {
      if(result.success) {
        this._loadUserData();
      }
      if(result.success && this.newFile) {
        this.saveAvatar();
      }
      else if(!this.newFile) {
        this.serviceResultHelper.handleServiceResult(result, "Profile data updated succesfully", "Errors occured");
      }
    });
  }

  saveAvatar() {
    if(this.newFile) {
      this.service.saveImg(this.newFile).subscribe(result => {
        this.serviceResultHelper.handleServiceResult(result, "Profile data updated succesfully", "Errors occured");
        if(result.success) {
          this._loadImg();
        }
      });
    }
  }

}
