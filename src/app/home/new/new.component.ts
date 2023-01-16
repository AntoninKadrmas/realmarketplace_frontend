import {Component, OnInit} from '@angular/core';
import {Page} from "@nativescript/core";

@Component({
  selector: 'app-new',
  templateUrl: './new.component.html',
  styleUrls: ['./new.component.scss']
})
export class NewComponent implements OnInit{
  constructor(private page:Page) {
  }
  ngOnInit(): void {
    this.page.actionBarHidden=true
  }


}
