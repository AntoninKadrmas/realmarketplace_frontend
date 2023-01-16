import {Component, OnInit} from '@angular/core';
import {Page} from "@nativescript/core";

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.scss']
})
export class SearchComponent implements OnInit{
  constructor(private page:Page) {
  }
  ngOnInit(){
    this.page.actionBarHidden=true
  }
}
