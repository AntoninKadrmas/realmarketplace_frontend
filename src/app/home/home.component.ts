import { Component, OnInit } from '@angular/core'

import {ActivatedRoute} from "@angular/router";
import { RouterExtensions } from '@nativescript/angular';
import {Page} from "@nativescript/core";

@Component({
  selector: 'ns-home',
  templateUrl: './home.component.html',
})
export class HomeComponent implements OnInit {
  constructor(private route:ActivatedRoute,
              private router:RouterExtensions,
               ) {}

  ngOnInit(): void {
    this.loadTabs()
  }
  private loadTabs(){
    setTimeout(()=>{
      this.router.navigate([{outlets:{
          new_outlet:['new'],
          search_outlet:['search'],
          favorite_outlet:['favorite']
        }}],{relativeTo:this.route})
    },1)
  }
}
